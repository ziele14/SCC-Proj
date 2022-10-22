package scc.data;

import java.time.LocalTime;

public class Auction {
    private String id;
    private String title;
    private String photo_id;
    private User owner;
    private LocalTime end_time;
    private int min_price;
    private Bid winner;

    public Auction(String id, String title, String photo_id, User owner, LocalTime end_time, int min_price, Bid winner, String status) {
        this.id = id;
        this.title = title;
        this.photo_id = photo_id;
        this.owner = owner;
        this.end_time = end_time;
        this.min_price = min_price;
        this.winner = winner;
        this.status = status;
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalTime getEnd_time() {
        return end_time;
    }

    public void setEnd_time(LocalTime end_time) {
        this.end_time = end_time;
    }

    public int getMin_price() {
        return min_price;
    }

    public void setMin_price(int min_price) {
        this.min_price = min_price;
    }

    public Bid getWinner() {
        return winner;
    }

    public void setWinner(Bid winner) {
        this.winner = winner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String status;

}
