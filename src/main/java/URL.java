import java.util.concurrent.CopyOnWriteArrayList;

public class URL {
    private String url;
    private int level;
    private CopyOnWriteArrayList<URL> subUrlList;

    public void addSubUrl(URL page) {
        if (this.subUrlList == null) {
            this.subUrlList = new CopyOnWriteArrayList<>();
        }
        this.subUrlList.add(page);
    }

    public URL(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public CopyOnWriteArrayList<URL> getSubUrlList() {
        return subUrlList;
    }

    public void setSubUrlList(CopyOnWriteArrayList<URL> subUrlList) {
        this.subUrlList = subUrlList;
    }
}
