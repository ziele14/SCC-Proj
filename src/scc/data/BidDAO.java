package scc.data;

import java.util.Arrays;

public class BidDAO {
    private String _rid;
    private String _ts;
    private Auction auction;
    private User bid_maker;
    private int bid_value;

    public BidDAO(Auction auction, User bid_maker) {
        this.auction = auction;
        this.bid_maker = bid_maker;
    }
    public String get_rid() {
        return _rid;
    }

    public void set_rid(String _rid) {
        this._rid = _rid;
    }

    public String get_ts() {
        return _ts;
    }

    public void set_ts(String _ts) {
        this._ts = _ts;
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

    public String toString() {
        String var10000 = this._rid;
        return "BidDAO [_rid=" + var10000 + ", _ts=" + this._ts + ", auction=" + this.auction + ", bid_maker=" + this.bid_maker + ", bid value=" + this.bid_value + "]";

    }
}
