package scc.data;

public class Question {
    private String auctionId;
    private String text;
    private String userId;
    private String id;

    public Question(String text, String userId, String id) {
        this.text = text;
        this.userId = userId;
        this.id = id;
    }

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Question{" +
                "auctionId='" + auctionId + '\'' +
                ", text='" + text + '\'' +
                ", userId='" + userId + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
