package scc.data;

import java.util.ArrayList;

public class AuctionDAO {

    private String _rid;
    private String _ts;
    private String id;
    private String title;
    private String photoId;
    private String description;
    private String ownerId;
    private String endTime;
    private int minPrice;
    private String winner;
    private String status;
    private ArrayList<String> listOfBids;
    private ArrayList<String> listOfQuestions;

    public AuctionDAO() {
    }

    public AuctionDAO(Auction a) {
        this(a.getId(), a.getTitle(), a.getDescription(),a.getPhotoId(),a.getOwnerId(),a.getEnd_time(),a.getMin_price());
    }
    public AuctionDAO( String id,String title, String description, String photoId, String ownerId,String endTime, int minPrice) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.photoId = photoId;
        this.ownerId = ownerId;
        this.endTime = endTime;
        this.minPrice = minPrice;

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

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public ArrayList<String> getListOfBids() {
        return listOfBids;
    }

    public ArrayList<String> getListOfQuestions() {
        return listOfQuestions;
    }

    public void setListOfQuestions(ArrayList<String> listOfQuestions) {
        this.listOfQuestions = listOfQuestions;
    }

    public void setListOfBids(ArrayList<String> listOfBids) {
        this.listOfBids = listOfBids;
    }
    public void addBid(String bidId){this.listOfBids.add(bidId);}
    public void removeBid(String bid){this.listOfBids.remove(bid);}
    public void addQuestion(String questionId) {this.listOfQuestions.add(questionId);}

    public int getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(int minPrice) {
        this.minPrice = minPrice;
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
        return new Auction(this.id, this.title,this.description,this.photoId,this.ownerId,this.endTime,this.minPrice, this.winner,this.status,this.listOfBids,this.listOfQuestions);
    }

    @Override
    public String toString() {
        return "AuctionDAO{" +
                "_rid='" + _rid + '\'' +
                ", _ts='" + _ts + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", photoId='" + photoId + '\'' +
                ", description='" + description + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", endTime='" + endTime + '\'' +
                ", minPrice=" + minPrice +
                ", winner='" + winner + '\'' +
                ", status='" + status + '\'' +
                //", listOfBids=" + listOfBids +
                //", listOfQuestions=" + listOfQuestions +
                '}';
    }
}
