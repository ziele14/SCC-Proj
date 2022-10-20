package scc.data;

public class Bid {
    private Auction auction;
    private User bid_maker;
    private int bid_value;

    public Bid(Auction auction, User bid_maker, int bid_value) {
        this.auction = auction;
        this.bid_maker = bid_maker;
        this.bid_value = bid_value;
    }

    public Auction getAuction() {
        return auction;
    }

    public void setAuction(Auction auction) {
        this.auction = auction;
    }

    public User getBid_maker() {
        return bid_maker;
    }

    public void setBid_maker(User bid_maker) {
        this.bid_maker = bid_maker;
    }

    public int getBid_value() {
        return bid_value;
    }

    public void setBid_value(int bid_value) {
        this.bid_value = bid_value;
    }
}
