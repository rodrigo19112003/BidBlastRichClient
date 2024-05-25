package api.requests.auctioncategories;

public class AuctionCategoryBody {
    private String title;
    private String description;
    private String keywords;

    public AuctionCategoryBody(String title, String description, String keywords) {
        this.title = title;
        this.description = description;
        this.keywords = keywords;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}