package scc.srv;

import com.azure.core.annotation.Get;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import scc.data.*;
import scc.utils.Hash;

import java.util.ArrayList;


@Path("/auction")
public class AuctionResource {
    CosmoDBLayer db = CosmoDBLayer.getInstance();

    @Path("/about_to_close")
    @GET
    public String list_auctions(){
       return "Aukcje które się zamykają";
    }
    @Path("/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String auction_create(String input){
        Gson gson = new Gson();
       try {
            AuctionDAO auctionDAO = gson.fromJson(input, AuctionDAO.class);
            db.putAuction(auctionDAO);
            db.close();
            return "Auction created, ID : " + auctionDAO.getId() + ", title : " + auctionDAO.getTitle();
      }
      catch(Exception e){
          return "The input auction data seems to be invalid or the ID is already taken";
       }
    }


    @Path("/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String update_auction(@PathParam("id")String id, String inpucik){
        Gson gson = new Gson();
        try {
            AuctionDAO auctionDAO = gson.fromJson(inpucik, AuctionDAO.class);
            db.delAuctionById(id);
            db.putAuction(auctionDAO);
            db.close();
            return "Auction updated, new values : " + auctionDAO.toAuction().toString();
        }
        catch(Exception e){
            return "There is no auction with this ID or the data has invalid form";
        }

    }


    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String auction_get(){
        CosmosPagedIterable<AuctionDAO> resGet = db.getAuctions();
        ArrayList<String> auctions = new ArrayList<String>();
        for( AuctionDAO e: resGet) {
            auctions.add(e.toAuction().toString());
        }
        db.close();
        if(auctions.size() == 0){
            return "It seems there are no auctions in the database";
        }
        else {
            return auctions.toString();
        }
    }


    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String object_get_id(@PathParam("id") String id){
        CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(id);
        ArrayList<String> auction = new ArrayList<String>();
        for( AuctionDAO e: res) {
            auction.add(e.toAuction().toString());
        }
        db.close();
        if (auction.size() == 0) {
            return "There is no such auction";
        }
        return auction.get(0).toString();
    }




    @Path("/{id}/bid")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String bid_create(@PathParam("id") String id,String input){
        /**bierze ID aukcji, sprawdza czy taka istnieja*/
        CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(id);
        ArrayList<AuctionDAO> auction = new ArrayList<AuctionDAO>();
        for( AuctionDAO e: res) {
            auction.add(e);
        }
        if (auction.size() == 0) {
            return "There is no such auction here";
        }
        /** tworzy gsona, zczytuje dane, sprawdza czy ID aukcji w jsonie jest takie samo jak powinno, sprawdza czy cena się zgadza*/
        Gson gson = new Gson();
        BidDAO bidDAO = gson.fromJson(input,BidDAO.class);
        bidDAO.setAuctionId(id);
        /** sprawdza czy taki user istnieje*/
        CosmosPagedIterable<UserDAO> result = db.getUserById(bidDAO.getUserId());
        ArrayList<String> users = new ArrayList<String>();
        for( UserDAO e: result) {
            users.add(e.toUser().toString());
        }
        if (users.size() == 0) {
            return "There is no such user here :/";
        }

        if (bidDAO.getBid_value() <= auction.get(0).getMinPrice()){
            return "This bid is too small, you need to pay more than " + auction.get(0).getMinPrice();
        }
        else{
            /** ustawia na ID userID + wartość i potem wkłada a aukcję zamienia na taką z dobrą listą bidów*/
            bidDAO.setId(bidDAO.getUserId() + " has pose a bid with value of :" + bidDAO.getBid_value());
            db.putBid(bidDAO);
            db.delAuctionById(auction.get(0).getId());
            auction.get(0).setMinPrice(bidDAO.getBid_value());
            try{
            auction.get(0).addBid(bidDAO.getId());
                }
            catch(Exception e){
                auction.get(0).setListOfBids(new ArrayList<String>());
                auction.get(0).addBid(bidDAO.getId());

            }
            db.putAuction(auction.get(0));
            db.close();
            return "You have created a bid";}

    }

    @Path("/{id}/bid")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String list_bids(@PathParam("id") String id){
        CosmosPagedIterable<AuctionDAO> resGet = db.getAuctionById(id);
        ArrayList<String> bids = new ArrayList<String>();
        for( AuctionDAO e: resGet) {
            if (e.getListOfBids() == null){
                return "There are currently no bids";
            }
            bids.add(e.getListOfBids().toString());
        }

        db.close();
        return bids.get(0);
    }

    @Path("/{id}/question")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String question_create(@PathParam("id") String id,String input){
        CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(id);
        ArrayList<AuctionDAO> auction = new ArrayList<AuctionDAO>();
        for( AuctionDAO e: res) {
            auction.add(e);
        }
        if (auction.size() == 0) {
            return "There is no such auction here";
        }
        Gson gson=new Gson();
        QuestionDAO questionDAO=gson.fromJson(input,QuestionDAO.class);
        questionDAO.setAuctionId(id);
        CosmosPagedIterable<UserDAO> result = db.getUserById(questionDAO.getUserId());
        ArrayList<String> users = new ArrayList<String>();
        for( UserDAO e: result) {
            users.add(e.toUser().toString());
        }
        if (users.size() == 0) {
            return "There is no such user here :/";
        }
        db.putQuestion(questionDAO);
        db.delAuctionById(auction.get(0).getId());
        try{
            auction.get(0).addQuestion(questionDAO.getText() + " " + ", from user : " + questionDAO.getId());
        }
        catch(Exception e){
            auction.get(0).setListOfQuestions(new ArrayList<String>());
            auction.get(0).addQuestion(questionDAO.getText() + " " + ", from user : " + questionDAO.getId());

        }
        db.putAuction(auction.get(0));
        db.close();
        return "You have created a question";
    }

    @Path("/{id}/question")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String list_questions(@PathParam("id")String id){
        CosmosPagedIterable<AuctionDAO> resGet = db.getAuctionById(id);
        ArrayList<String> questions = new ArrayList<String>();
        for( AuctionDAO e: resGet) {
            if (e.getListOfBids() == null){
                return "There are currently no questions";
            }
            questions.add(e.getListOfQuestions().toString());
        }
        db.close();
        return questions.get(0);

    }







}

