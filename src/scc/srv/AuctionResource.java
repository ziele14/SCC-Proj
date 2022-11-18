package scc.srv;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.data.*;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;


@Path("/auction")
public class AuctionResource {
    CosmoDBLayer db = CosmoDBLayer.getInstance();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    Jedis jedis = RedisCache.getCachePool().getResource();
    private static AtomicInteger AUCTION_ID = new AtomicInteger(1);
    private static AtomicInteger QUESTION_ID = new AtomicInteger(1);


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
            auctionDAO.setListOfQuestions(new ArrayList<QuestionDAO>());
            Integer index = AUCTION_ID.getAndIncrement();
            auctionDAO.setId(index.toString());
            LocalDateTime auctionTime = LocalDateTime.parse(auctionDAO.getEndTime(), formatter);
            if (auctionTime.isBefore(LocalDateTime.now())){
                return "The date is not valid. Date should be before now. \nProvided date: " + auctionTime.toString() + "\nNow: " + LocalDateTime.now().toString();
            }
           checkCookieUser(session, auctionDAO.getOwner());
           CosmosPagedIterable<UserDAO> result = db.getUserById(auctionDAO.getOwner());
           ArrayList<String> users = new ArrayList<String>();
           for( UserDAO e: result) {
               users.add(e.toUser().toString());
           }
           if (users.size() == 0) {
               return "There is no such user here :/";
           }
            db.putAuction(auctionDAO);
            db.close();
//            return "Auction created, ID : " + auctionDAO.getId() + ", title : " + auctionDAO.getTitle() + ", status : " + auctionDAO.getStatus()+ ", owner : " + auctionDAO.getOwner();
           return gson.toJson(auctionDAO);
      }
       catch( WebApplicationException e) {
           throw e;
       }
       catch(Exception e){
          return "The input auction data seems to be invalid or the ID is already taken\n Provided: " + gson.fromJson(input, AuctionDAO.class).toAuction().toString();
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
            auctionDAO.setId(id);
            /** bierze tę aukcję*/
            CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(id);
            ArrayList<AuctionDAO> auction = new ArrayList<AuctionDAO>();
            for( AuctionDAO e: res) {
                auction.add(e);
            }
            if(auction.size() == 0){
                return "There's no such action";
            }
            checkCookieUser(session, auction.get(0).getOwner());
            AuctionDAO result = mergeObjects(auctionDAO,auction.get(0));
            db.updateAuction(result);
            db.close();
            return "Auction updated, new values : " + result.toAuction().toString();
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
    public String auctionGetAll(@QueryParam("status") String status){
            CosmosPagedIterable<AuctionDAO> resGet = db.getAuctions(status);
            ArrayList<String> auctions = new ArrayList<String>();
            for (AuctionDAO e : resGet) {
                auctions.add(e.toAuction().toString());
            }
            db.close();
            if (auctions.size() == 0) {
                return "It seems as if the auctions have disappeared :o";
            } else {
                return auctions.toString();
            }
    }

    @Path("/any/recent")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String auctionGetRecent(@QueryParam("len") Integer len, @QueryParam("st") Integer off){
        CosmosPagedIterable<AuctionDAO> resGet = db.getRecentAuctions(len, off);
        ArrayList<String> auctions = new ArrayList<String>();
        for (AuctionDAO e : resGet) {
            auctions.add(e.toAuction().toString());
        }
        db.close();
        if (auctions.size() == 0) {
            return "It seems as if the auctions have disappeared :o";
        } else {
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
        if (bidDAO.getUser() == null){
            return "Wrong input lad";
        }
        /** sprawdza czy taki user istnieje*/
        CosmosPagedIterable<UserDAO> result = db.getUserById(bidDAO.getUser());
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
            bidDAO.setId(bidDAO.getUser() + " : " + bidDAO.getBid_value());
            try {
                checkCookieUser(session, bidDAO.getUser());
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
    public String listBids(@PathParam("id") String id, @QueryParam("st") Integer st, @QueryParam("len") Integer len){
        CosmosPagedIterable<AuctionDAO> resGet = db.getAuctionById(id);
        List<BidDAO> bids = new ArrayList<BidDAO>();
        for( AuctionDAO e: resGet) {
            if (e.getListOfBids() == null || Objects.equals(e.getListOfBids().size(), 0)){
                return "There are currently no bids lad, go ahead then";
            }
//            bids.add(e.getListOfBids().toString());
            bids = e.getListOfBids();
            Collections.reverse(bids);
        }
        db.close();
        if (len == null || st == null){
            return bids.toString();
        }
        List<BidDAO> res = bids.subList(st, st + len);
        return res.toString();
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
        Integer index = QUESTION_ID.getAndIncrement();
        questionDAO.setId(index.toString());
        questionDAO.setReply(null);
        CosmosPagedIterable<UserDAO> result = db.getUserById(questionDAO.getUser());
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
        return gson.toJson(questionDAO);
//        return "You have created a question";
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
        CosmosPagedIterable<UserDAO> result = db.getUserById(questionDAO.getUser());
        ArrayList<User> users = new ArrayList<User>();
        for( UserDAO e: result) {
            users.add(e.toUser());
        }
        if (users.size() == 0) {
            return "There is no such user here :/";
        }
        if (!users.get(0).getId().equals(auction.get(0).getOwner())){
            return "You cannot do that mate, you ain't the auction owner";
        }
        try {
            checkCookieUser(session, users.get(0).getId());
        }
        catch( WebApplicationException e) {
            throw e;
        }
        String reply = questionDAO.getReply();
        for (QuestionDAO question : auction.get(0).getListOfQuestions()){
            if (question.getId().equals(questionID)){
                if(question.getReply() == null){
                    question.setReply(reply);
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
        CosmosPagedIterable<AuctionDAO> result = db.getAuctions("open");
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

    public static <T> T mergeObjects(T first, T second){
        Class<?> clas = first.getClass();
        Field[] fields = clas.getDeclaredFields();
        Object result = null;
        try {
            result = clas.getDeclaredConstructor().newInstance();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value1 = field.get(first);
                Object value2 = field.get(second);
                Object value = (value1 != null) ? value1 : value2;
                field.set(result, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) result;
    }






}

