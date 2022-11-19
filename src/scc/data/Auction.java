package scc.data;


import java.util.ArrayList;

public class Auction {
    private String id;
    private String title;
    private String imageId;
    private String description;
    private String owner;
    private String end_time;
    private float min_price;
    private String winner;
    private String status;
    private ArrayList<BidDAO> listOfBids;
    private ArrayList<QuestionDAO> listOfQuestions;

    public Auction(String id, String title, String imageId, String description, String owner, String end_time, float min_price, String winner, String status, ArrayList<BidDAO> listOfBids, ArrayList<QuestionDAO> listOfQuestions) {
        this.id = id;
        this.title = title;
        this.imageId = imageId;
        this.description = description;
        this.owner = owner;
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
                ", imageId='" + imageId + '\'' +
                ", description='" + description + '\'' +
                ", ownerId='" + owner + '\'' +
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

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public float getMin_price() {
        return min_price;
    }

    public void setMin_price(float min_price) {
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

    public ArrayList<QuestionDAO> getListOfQuestions() {
        return listOfQuestions;
    }

    public void setListOfQuestions(ArrayList<QuestionDAO> listOfQuestions) {
        this.listOfQuestions = listOfQuestions;
    }
}
