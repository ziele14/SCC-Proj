package scc.data;

import java.util.ArrayList;

public class AuctionDAO {

    private String _rid;
    private String _ts;
    private String id;
    private String title;
    private String imageId;
    private String description;
    private String owner;
    private String endTime;
    private int minPrice;
    private String winner;
    private String status;
    private ArrayList<BidDAO> listOfBids;
    private ArrayList<QuestionDAO> listOfQuestions;

    public AuctionDAO() {
    }

    public AuctionDAO(Auction a) {
        this(a.getTitle(), a.getDescription(),a.getImageId(),a.getOwner(),a.getEnd_time(),a.getMin_price());
        this.status = a.getStatus();
    }

    public AuctionDAO(String title, String description, String imageId, String owner, String endTime, int minPrice) {
        this.title = title;
        this.description = description;
        this.imageId = imageId;
        this.owner = owner;
        this.endTime = endTime;
        this.minPrice = minPrice;
        this.status = "open";

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

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public ArrayList<BidDAO> getListOfBids() {
        return listOfBids;
    }

    public ArrayList<QuestionDAO> getListOfQuestions() {
        return listOfQuestions;
    }

    public void setListOfQuestions(ArrayList<QuestionDAO> listOfQuestions) {
        this.listOfQuestions = listOfQuestions;
    }

    public void setListOfBids(ArrayList<BidDAO> listOfBids) {
        this.listOfBids = listOfBids;
    }

    public void addBid(BidDAO bid){this.listOfBids.add(bid);}

    public void removeBid(BidDAO bid){this.listOfBids.remove(bid);}

    public void addQuestion(QuestionDAO questionId) {this.listOfQuestions.add(questionId);}

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
        return new Auction(this.id, this.title,this.imageId, this.description, this.owner,this.endTime,this.minPrice, this.winner,this.status,this.listOfBids,this.listOfQuestions);
    }

    public void AuctionClose(){
        this.status = "closed";
        if (listOfBids.isEmpty()) {
            this.winner = "No winner";
        }
        else {
            this.winner = listOfBids.get(listOfBids.size() - 1).getId();
        }
    }

    @Override
    public String toString() {
        return "AuctionDAO{" +
                "_rid='" + _rid + '\'' +
                ", _ts='" + _ts + '\'' +
                ", id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", imageId='" + imageId + '\'' +
                ", description='" + description + '\'' +
                ", ownerId='" + owner + '\'' +
                ", endTime='" + endTime + '\'' +
                ", minPrice=" + minPrice +
                ", winner='" + winner + '\'' +
                ", status='" + status + '\'' +
                ", listOfBids=" + listOfBids.toString() +
                ", listOfQuestions=" + listOfQuestions.toString() +
                '}';
    }
}
