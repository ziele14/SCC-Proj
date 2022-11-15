package scc.srv;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.data.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;


@Path("/auction")
public class AuctionResource {
    CosmoDBLayer db = CosmoDBLayer.getInstance();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    Jedis jedis = RedisCache.getCachePool().getResource();


    @Path("/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String auctionCreate(String input, @CookieParam("scc:session") Cookie session){
        Gson gson = new Gson();
       try {
            AuctionDAO auctionDAO = gson.fromJson(input, AuctionDAO.class);
            auctionDAO.setStatus("open");
            auctionDAO.setListOfBids(new ArrayList<BidDAO>());
            LocalDateTime auctionTime = LocalDateTime.parse(auctionDAO.getEndTime(), formatter);
            if (auctionTime.isBefore(LocalDateTime.now())){
                return "The date is not valid";
            }
           checkCookieUser(session, auctionDAO.getOwnerId());
           CosmosPagedIterable<UserDAO> result = db.getUserById(auctionDAO.getOwnerId());
           ArrayList<String> users = new ArrayList<String>();
           for( UserDAO e: result) {
               users.add(e.toUser().toString());
           }
           if (users.size() == 0) {
               return "There is no such user here :/";
           }
            db.putAuction(auctionDAO);
            db.close();
            return "Auction created, ID : " + auctionDAO.getId() + ", title : " + auctionDAO.getTitle() + ", status : " + auctionDAO.getStatus()+ ", owner : " + auctionDAO.getOwnerId();
      }
       catch( WebApplicationException e) {
           throw e;
       }
       catch(Exception e){
          return "The input auction data seems to be invalid or the ID is already taken";
       }
    }


    @Path("/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateAuction(@PathParam("id")String id, String input, @CookieParam("scc:session") Cookie session){
        Gson gson = new Gson();
        try {
            AuctionDAO auctionDAO = gson.fromJson(input, AuctionDAO.class);
            auctionDAO.setStatus("open");
            auctionDAO.setListOfBids(new ArrayList<BidDAO>());
            checkCookieUser(session, auctionDAO.getOwnerId());
            db.updateAuction(auctionDAO);
            db.close();
            return "Auction updated, new values : " + auctionDAO.toAuction().toString();
        }
        catch( WebApplicationException e) {
            throw e;
        }
        catch(Exception e){
            return "There is no auction with this ID or the data has invalid form";
        }

    }


    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String auctionGetAll(){
        CosmosPagedIterable<AuctionDAO> resGet = db.getAuctions();
        ArrayList<String> auctions = new ArrayList<String>();
        for( AuctionDAO e: resGet) {
            auctions.add(e.toAuction().toString());
        }
        db.close();
        if(auctions.size() == 0){
            return "It seems as if the auctions have disappeared :o";
        }
        else {
            return auctions.toString();
        }
    }


    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String auctionGetBtId(@PathParam("id") String id){
        CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(id);
        ArrayList<String> auction = new ArrayList<String>();
        for( AuctionDAO e: res) {
            auction.add(e.toAuction().toString());
        }
        db.close();
        if (auction.size() == 0) {
            return "The auction with this Id simply doesn't exist";
        }
        return auction.get(0);
    }




    @Path("/{id}/bid")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String bidCreate(@PathParam("id") String id,String input, @CookieParam("scc:session") Cookie session){
        /**bierze ID aukcji, sprawdza czy taka istnieja*/
        CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(id);
        ArrayList<AuctionDAO> auction = new ArrayList<AuctionDAO>();
        for( AuctionDAO e: res) {
            auction.add(e);
        }
        if (auction.size() == 0) {
            return "There is no such auction here";
        }
        if (!Objects.equals(auction.get(0).getStatus(),"open") ){
            return "The auction is not open";
        }
        if (LocalDateTime.parse(auction.get(0).getEndTime(), formatter).isBefore(LocalDateTime.now())){
            auction.get(0).AuctionClose();
            db.updateAuction(auction.get(0));
            return "You are too late mate";

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
            bidDAO.setId(bidDAO.getUserId() + " : " + bidDAO.getBid_value());
            try {
                checkCookieUser(session, bidDAO.getUserId());
            }
            catch( WebApplicationException e) {
                throw e;
            }
            auction.get(0).setMinPrice(bidDAO.getBid_value());
            try{
            auction.get(0).addBid(bidDAO);
                }
            catch(Exception e){
                auction.get(0).setListOfBids(new ArrayList<BidDAO>());
                auction.get(0).addBid(bidDAO);
            }
            db.updateAuction(auction.get(0));
            db.close();
            return "You have created a bid : " + bidDAO.getId();}

    }

    @Path("/{id}/bid")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String listBids(@PathParam("id") String id){
        CosmosPagedIterable<AuctionDAO> resGet = db.getAuctionById(id);
        ArrayList<String> bids = new ArrayList<String>();
        for( AuctionDAO e: resGet) {
            if (e.getListOfBids() == null || Objects.equals(e.getListOfBids().size(), 0)){
                return "There are currently no bids lad, go ahead then";
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
    public String questionCreate(@PathParam("id") String id,String input, @CookieParam("scc:session") Cookie session){
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
        CosmosPagedIterable<UserDAO> result = db.getUserById(questionDAO.getUserId());
        ArrayList<User> users = new ArrayList<User>();
        for( UserDAO e: result) {
            users.add(e.toUser());
        }
        if (users.size() == 0) {
            return "There is no such user here :/";
        }


        try {
            checkCookieUser(session, users.get(0).getId());
        }
        catch( WebApplicationException e) {
            throw e;
        }
        try{
            auction.get(0).addQuestion(questionDAO);
        }
        catch(Exception e){
            auction.get(0).setListOfQuestions(new ArrayList<QuestionDAO>());
            auction.get(0).addQuestion(questionDAO);

        }
        db.updateAuction(auction.get(0));
        db.close();
        return "You have created a question";
    }

    @Path("/{id}/question/{qid}/reply")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String questionAnswer(@PathParam("id") String auctionId,String input,@PathParam("qid") String questionID, @CookieParam("scc:session") Cookie session){
        CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(auctionId);
        ArrayList<AuctionDAO> auction = new ArrayList<AuctionDAO>();
        for( AuctionDAO e: res) {
            auction.add(e);
        }
        if (auction.size() == 0) {
            return "There is no such auction here";
        }
        Gson gson=new Gson();
        QuestionDAO questionDAO=gson.fromJson(input,QuestionDAO.class);
        CosmosPagedIterable<UserDAO> result = db.getUserById(questionDAO.getUserId());
        ArrayList<User> users = new ArrayList<User>();
        for( UserDAO e: result) {
            users.add(e.toUser());
        }
        if (users.size() == 0) {
            return "There is no such user here :/";
        }
        if (!users.get(0).getId().equals(auction.get(0).getOwnerId())){
            return "You cannot do that mate, you ain't the auction owner";
        }
        try {
            checkCookieUser(session, users.get(0).getId());
        }
        catch( WebApplicationException e) {
            throw e;
        }
        String reply = questionDAO.getAnswer();
        for (QuestionDAO question : auction.get(0).getListOfQuestions()){
            if (question.getId().equals(questionID)){
                if(question.getAnswer() == null){
                    question.setAnswer(reply);
                    db.updateAuction(auction.get(0));
                    db.close();
                    return "You have succesfully replied";
                }
                else{
                    return "The question was already answered";
                }
            }
        }
        return "There is no such question mate";
    }

    @Path("/{id}/question")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listQuestions(@PathParam("id")String id){
        CosmosPagedIterable<AuctionDAO> resGet = db.getAuctionById(id);
        ArrayList<String> questions = new ArrayList<String>();
        for( AuctionDAO e: resGet) {
            if (e.getListOfQuestions() == null){
                return "There are currently no questions";
            }
            for ( QuestionDAO question : e.getListOfQuestions()){
                questions.add(question.toQuestion().toString());
            }

        }
        db.close();
        return questions.toString();

    }

    @Path("/closing")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listAuctionsAboutToClose(){
        CosmosPagedIterable<AuctionDAO> result = db.getAuctions();
        ArrayList<Auction> closingAuctions = new ArrayList<Auction>();
        for( AuctionDAO a: result){
            if (Objects.equals(a.getStatus(),"open")){
                LocalDateTime auctionTime = LocalDateTime.parse(a.getEndTime(), formatter);
                Duration diff = Duration.between(auctionTime, LocalDateTime.now());
                if (Math.abs(diff.toDays()) <= 7){
                    closingAuctions.add(a.toAuction());
                }

            }

        }
        if(closingAuctions.size() == 0){
            return "No actions are close to the end";
        }
        return closingAuctions.toString();
    }


    /**
     * Throws exception if not appropriate user for operation on Auction
     */
    public String checkCookieUser(Cookie session, String id)
            throws NotAuthorizedException {
        if (session == null || session.getValue() == null)
            throw new NotAuthorizedException("No session initialized");
        String s;
        try {
            s = jedis.get(session.getValue());
        } catch (Exception e) {
            throw new NotAuthorizedException("No valid session initialized");
        }
        if (s == null || s == null || s.length() == 0)
            throw new NotAuthorizedException("No valid session initialized");
        if (!s.equals(id) && !s.equals("admin"))
            throw new NotAuthorizedException("Invalid user : " + s);
        return s;
    }






}

