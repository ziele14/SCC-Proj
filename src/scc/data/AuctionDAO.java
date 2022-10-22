package scc.data;

import scc.utils.Hash;

import java.util.Arrays;

public class AuctionDAO {

    private String _rid;
    private String _ts;
    private String id;
    private String title;
    private String photo_id;
    private String description;
    private String ownerID;
    private String end_time;
    private int min_price;
    private String winner;
    private String status;

    public AuctionDAO( String id,String title, String description, String photoId, String owner,String endTime, int minPrice) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.photo_id = photoId;
        this.ownerID = owner;
        this.end_time = endTime;
        this.min_price = minPrice;
        this.status = "open";
        this.winner = null;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public int getMin_price() {
        return min_price;
    }

    public void setMin_price(int min_price) {
        this.min_price = min_price;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public Auction toAuction(){
        return new Auction(this.title,this.description,this.photo_id,this.ownerID,this.end_time,this.min_price,this.status);
    }

    @Override
    public String toString() {
        return "AuctionDAO{" +
                "_rid='" + _rid + '\'' +
                ", _ts='" + _ts + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", description='" + description + '\'' +
                ", ownerID='" + ownerID + '\'' +
                ", end_time='" + end_time + '\'' +
                ", min_price=" + min_price +
                ", status='" + status + '\'' +
                '}';
    }
}
