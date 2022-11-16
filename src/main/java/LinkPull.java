import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class LinkPull extends RecursiveTask<URL> {
    private String parentUrl;
    private URL url;
    private volatile Set<String> usedUrl;
    private volatile Queue<URL> queue;

    public LinkPull(String parentUrl) {
        this.parentUrl = parentUrl;
        this.url = new URL(parentUrl);
        url.setLevel(0);
        usedUrl = Collections.synchronizedSet(new HashSet<>());
        usedUrl.add(parentUrl);
        this.queue = new ConcurrentLinkedQueue<>();
    }

    public LinkPull(String parentUrl, Set<String> usedUrl, URL url, Queue<URL> queue) {
        this.parentUrl = parentUrl;
        this.usedUrl = usedUrl;
        this.url = url;
        this.queue = queue;
    }

    @Override
    protected URL compute() {
        List<LinkPull> taskList = new ArrayList<>();
        String currentUrl = url.getUrl();
        HashSet<String> links = getUrlList(currentUrl);
        for (String link : links) {
            if (!usedUrl.contains(link)) {
                URL newPage = new URL(link);
                newPage.setLevel(url.getLevel() + 1);
                queue.add(newPage);
                usedUrl.add(link);
            }
        }

        while (queue.peek() != null) {
            URL tempPage = queue.poll();
            LinkPull task = new LinkPull(parentUrl, usedUrl, tempPage, queue);
            url.addSubUrl(tempPage);
            task.fork();
            taskList.add(task);
        }

        taskList.forEach(ForkJoinTask::join);

        return url;
    }

    public HashSet<String> getUrlList(String url) {
        HashSet<String> urlList = new HashSet<>();
        System.out.println("Parsing URL with address: " + parentUrl);
        try {
            Thread.sleep(150);
            Document doc = Jsoup.connect(parentUrl).get();
            Elements elements = doc.select("a");

            for (Element el : elements) {
                String attr = el.attr("abs:href");
                if (!attr.isEmpty() && !attr.contains("#") && !usedUrl.contains(attr) && attr.startsWith(parentUrl)) {
                    LinkPull childUrl = new LinkPull(attr);
                    childUrl.fork();
                    urlList.add(attr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlList;
    }


}

