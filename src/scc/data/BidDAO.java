package scc.data;

public class BidDAO {
    private String _rid;
    private String _ts;
    private String id;
    private String auctionId;
    private String user;
    private int bid_value;

    public BidDAO() {
    }

    public BidDAO(Bid b) {
        this();
    }

    public BidDAO(String user, int bid_value) {
        this.user = user;
        this.bid_value = bid_value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(String auctionId) {
        this.auctionId = auctionId;
    }

    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getBid_value() {
        return bid_value;
    }

    public void setBid_value(int bid_value) {
        this.bid_value = bid_value;
    }

    public Bid toBid(){
        return new Bid(this.id,this.auctionId,this.user,this.bid_value);
    }
}
