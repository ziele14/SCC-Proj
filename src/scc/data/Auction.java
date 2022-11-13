package scc.data;


import java.util.ArrayList;

public class Auction {
    private String id;
    private String title;
    private String photoId;
    private String description;
    private String ownerId;
    private String end_time;
    private int min_price;
    private String winner;
    private String status;
    private ArrayList<BidDAO> listOfBids;
    private ArrayList<String> listOfQuestions;

    public Auction(String id, String title, String photoId, String description, String ownerId, String end_time, int min_price, String winner, String status, ArrayList<BidDAO> listOfBids, ArrayList<String> listOfQuestions) {
        this.id = id;
        this.title = title;
        this.photoId = photoId;
        this.description = description;
        this.ownerId = ownerId;
        this.end_time = end_time;
        this.min_price = min_price;
        this.winner = winner;
        this.status = status;
        this.listOfBids = listOfBids;
        this.listOfQuestions = listOfQuestions;
    }

    @Override
    public String toString() {
        return "Auction = (" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", photoId='" + photoId + '\'' +
                ", description='" + description + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", end_time='" + end_time + '\'' +
                ", min_price=" + min_price +
                ", winner='" + winner + '\'' +
                ", status='" + status + '\'' +
                ", listOfBids=" + listOfBids +
                ", listOfQuestions=" + listOfQuestions +
                ')';
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

    public ArrayList<BidDAO> getListOfBids() {
        return listOfBids;
    }

    public void setListOfBids(ArrayList<BidDAO> listOfBids) {
        this.listOfBids = listOfBids;
    }

    public ArrayList<String> getListOfQuestions() {
        return listOfQuestions;
    }

    public void setListOfQuestions(ArrayList<String> listOfQuestions) {
        this.listOfQuestions = listOfQuestions;
    }
}
