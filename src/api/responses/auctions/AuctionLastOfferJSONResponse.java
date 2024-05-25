package api.responses.auctions;

public class AuctionLastOfferJSONResponse {
    private int id;
    private float amount;
    private String creationDate;

    public AuctionLastOfferJSONResponse() { }

    public AuctionLastOfferJSONResponse(int id, float amount, String creationDate) {
        this.id = id;
        this.amount = amount;
        this.creationDate = creationDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}