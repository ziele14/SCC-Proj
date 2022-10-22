package scc.data;

public class Questions {
    public Auction auction;
    public String text;
    public User user_that_posted;

    public Questions(Auction auction, String text, User user_that_posted) {
        this.auction = auction;
        this.text = text;
        this.user_that_posted = user_that_posted;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser_that_posted() {
        return user_that_posted;
    }

    public void setUser_that_posted(User user_that_posted) {
        this.user_that_posted = user_that_posted;
    }
}
