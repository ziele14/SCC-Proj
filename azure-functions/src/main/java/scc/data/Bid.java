package scc.data;

public class Bid {
    private String id;
    private String auctionId;
    private String user;
    private float bid_value;

    public Bid(String id, String auctionId, String user, float bid_value) {
        this.id = id;
        this.auctionId = auctionId;
        this.user = user;
        this.bid_value = bid_value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public float getBid_value() {
        return bid_value;
    }

    public void setBid_value(float bid_value) {
        this.bid_value = bid_value;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "id='" + id + '\'' +
                ", auctionId='" + auctionId + '\'' +
                ", userId='" + user + '\'' +
                ", bid_value=" + bid_value +
                '}';
    }
}