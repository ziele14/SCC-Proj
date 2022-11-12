package scc.data;

public class Bid {
    private String id;
    private String auctionId;
    private String userId;
    private int bid_value;

    public Bid(String id,String auctionId, String userId, int bid_value) {
        this.id = id;
        this.auctionId = auctionId;
        this.userId = userId;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getBid_value() {
        return bid_value;
    }

    public void setBid_value(int bid_value) {
        this.bid_value = bid_value;
    }

    @Override
    public String toString() {
        return "Bid{" +
                "id='" + id + '\'' +
                ", auctionId='" + auctionId + '\'' +
                ", userId='" + userId + '\'' +
                ", bid_value=" + bid_value +
                '}';
    }
}