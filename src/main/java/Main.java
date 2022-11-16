
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public class Main {
    private static String url = "https://metanit.com/java/android";
    private static ForkJoinPool forkJoinPool;
    private static int processorCoreCount = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {

        Thread workThread = new Thread(() -> {

            forkJoinPool = new ForkJoinPool(processorCoreCount);
            URL link = forkJoinPool.invoke(new LinkPull(url));

            writeStringToFile(buildStringsFromPage(link));
        });

        workThread.start();
    }

    private static ArrayList<String> buildStringsFromPage(URL url) {
        final String tab = "\t";
        ArrayList<String> resultList = new ArrayList<>();

        resultList.add(tab.repeat(Math.max(0, url.getLevel())) + url.getUrl() + "\n");
        if (url.getSubUrlList() != null) {
            for (URL outUrl : url.getSubUrlList()) {
                ArrayList<String> tempStringList = buildStringsFromPage(outUrl);
                resultList.addAll(tempStringList);
            }
        }
        return resultList;
    }


    private static void writeStringToFile(List<String> stringList) {
        try {
            String filePath = "map/map.txt";
            Files.deleteIfExists(Paths.get(filePath));
            Files.createFile(Path.of(filePath));
            File file = new File(filePath);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String s : stringList) {
                writer.write(s);
            }
            writer.close();
            System.out.println("Файл записан в - " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
