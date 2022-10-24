package scc.data;


import java.util.ArrayList;

public class Auction {
    private String id;
    private String title;
    private String photo_id;
    private String description;
    private String ownerID;
    private String end_time;
    private int min_price;
    private String winner;
    private String status;
    private ArrayList<String> listOfBids;

    public Auction(String id,String title, String description, String photo_id, String owner,String end_time, int min_price,String winner, String status, ArrayList<String> listOfBids) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.photo_id = photo_id;
        this.ownerID = owner;
        this.end_time = end_time;
        this.min_price = min_price;
        this.listOfBids = listOfBids;
        this.status = status;
        this.winner = winner;
    }

    @Override
    public String toString() {
        return "Auction{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", description='" + description + '\'' +
                ", ownerID='" + ownerID + '\'' +
                ", end_time='" + end_time + '\'' +
                ", min_price=" + min_price +
                ", winner='" + winner + '\'' +
                ", status='" + status + '\'' +
                ", listOfBids=" + listOfBids +
                '}';
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

    public ArrayList<String> getListOfBids() {
        return listOfBids;
    }

    public void setListOfBids(ArrayList<String> listOfBids) {
        this.listOfBids = listOfBids;
    }
}
