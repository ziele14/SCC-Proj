package scc.srv;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
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
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static AtomicInteger AUCTION_ID = new AtomicInteger(1);
    private static AtomicInteger QUESTION_ID = new AtomicInteger(1);



    /**
     *
     * @param input json with the auction details
     * @param session cookie to check authentication
     * @return json with values of the auction or appropriate error
     */
    @Path("/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String auctionCreate(String input, @CookieParam("scc:session") Cookie session){
        Gson gson = new Gson();
       try {
           CosmoDBLayer db = CosmoDBLayer.getInstance();
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
           db.putAuction(auctionDAO);
           db.close();
           return gson.toJson(auctionDAO.toAuction());
      }
       catch( WebApplicationException e) {
           throw e;
       }
       catch(Exception e){
          return "The input auction data seems to be invalid or the ID is already taken\n Provided: " + gson.fromJson(input, AuctionDAO.class).toAuction().toString();
       }
    }




    /**
     *
     * @param id of the auction
     * @param input json
     * @param session token
     * @return updated values
     */
    @Path("/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateAuction(@PathParam("id")String id, String input, @CookieParam("scc:session") Cookie session){
        Gson gson = new Gson();
        try {
            CosmoDBLayer db = CosmoDBLayer.getInstance();
            AuctionDAO auctionDAO = gson.fromJson(input, AuctionDAO.class);
            auctionDAO.setId(id);
            CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(id);
            AuctionDAO auction = res.iterator().next();
            checkCookieUser(session, auction.getOwner());
            AuctionDAO result = mergeObjects(auctionDAO,auction);
            db.updateAuction(result);
            db.close();
            return gson.toJson(result);
        }
        catch( WebApplicationException e) {
            throw e;
        }
        catch(Exception e){
            return "There is no auction with this ID or the data has invalid form";
        }

    }



    /**
     * @param status additional query parameter regarding the status of the function
     * @return json of the auctions
     */
    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String auctionGetAll(@QueryParam("status") String status){
            CosmoDBLayer db = CosmoDBLayer.getInstance();
            Gson gson = new Gson();
            CosmosPagedIterable<AuctionDAO> resGet = db.getAuctions(status);
            ArrayList<Auction> auctions = new ArrayList<>();
            for (AuctionDAO e : resGet) {
                auctions.add(e.toAuction());
            }
            db.close();
            if (auctions.size() == 0) {
                return "It seems as if the auctions have disappeared :o";
            } else {
                return gson.toJson(auctions);
            }
    }



    /**
     *
     * returns recent auctions
     */
    @Path("/any/recent")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String auctionGetRecent(@QueryParam("len") Integer len, @QueryParam("st") Integer off){
        CosmoDBLayer db = CosmoDBLayer.getInstance();
        Gson gson = new Gson();
        CosmosPagedIterable<AuctionDAO> resGet = db.getRecentAuctions(len, off);
        ArrayList<Auction> auctions = new ArrayList<>();
        for (AuctionDAO e : resGet) {
            auctions.add(e.toAuction());
        }
        db.close();
        if (auctions.size() == 0) {
            return "It seems as if the auctions have disappeared :o";
        } else {
            return gson.toJson(auctions);
        }
    }



    /**
     *
     * @param id of the auction we wish to obtain information about
     * @return json of the auction
     */
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String auctionGetBtId(@PathParam("id") String id){
        CosmoDBLayer db = CosmoDBLayer.getInstance();
        Gson gson = new Gson();
        CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(id);
        try {
            Auction auction = res.iterator().next().toAuction();
            return gson.toJson(auction);
        }
        catch(Exception e){
            return "The auction like that simply doesn't exist";
        }
    }


    /**
     *
     * @param id of the auction we want to put a bid to
     * @param input json of the biDAO object
     * @param session cookie token
     * @return json of the bid
     */
    @Path("/{id}/bid")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String bidCreate(@PathParam("id") String id,String input, @CookieParam("scc:session") Cookie session){
        CosmoDBLayer db = CosmoDBLayer.getInstance();
        CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(id);
        /** chceck if the auction exists*/
        if (res.iterator().hasNext()) {
            AuctionDAO auction = res.iterator().next();
            if (!Objects.equals(auction.getStatus(), "open")) {
                return "The auction is not open";
            }
            if (LocalDateTime.parse(auction.getEndTime(), formatter).isBefore(LocalDateTime.now())) {
                auction.AuctionClose();
                db.updateAuction(auction);
                return "You are too late mate";
            }
            /** creates bid object from input*/
            Gson gson = new Gson();
            BidDAO bidDAO = gson.fromJson(input, BidDAO.class);
            bidDAO.setAuctionId(id);
            if (bidDAO.getUser() == null) {
                return "Wrong input lad";
            }
            CosmosPagedIterable<UserDAO> result = db.getUserById(bidDAO.getUser());
            if (!result.iterator().hasNext()) {
                return "There is no such user here :/";
            }
            if (bidDAO.getBid_value() <= auction.getMinPrice()) {
                return "This bid is too small, you need to pay more than " + auction.getMinPrice() + " and you provided: " + bidDAO.getBid_value();
            } else {
                /** sets the bid id */
                bidDAO.setId(bidDAO.getUser() + " : " + bidDAO.getBid_value());
                try {
                    checkCookieUser(session, bidDAO.getUser());
                } catch (WebApplicationException e) {
                    throw e;
                }
                auction.setMinPrice(bidDAO.getBid_value());
                /** checks if the bid list is created properly (should be) */
                try {
                    auction.addBid(bidDAO);
                } catch (Exception e) {
                    auction.setListOfBids(new ArrayList<BidDAO>());
                    auction.addBid(bidDAO);
                }
                db.updateAuction(auction);
                db.close();
                return gson.toJson(bidDAO.toBid());
            }
        }
        else {
            return "There is no such auction here";
        }

    }




    /**
     *
     * @param id of the function
     * @param st
     * @param len
     * @return gson of the resulting list of bids
     */
    @Path("/{id}/bid")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String listBids(@PathParam("id") String id, @QueryParam("st") Integer st, @QueryParam("len") Integer len){
        Gson gson = new Gson();
        CosmoDBLayer db = CosmoDBLayer.getInstance();
        CosmosPagedIterable<AuctionDAO> resGet = db.getAuctionById(id);
        List<Bid> bids = new ArrayList<Bid>();
        for( AuctionDAO e: resGet) {
            if (e.getListOfBids() == null || Objects.equals(e.getListOfBids().size(), 0)){
                return gson.toJson(bids);
            }
            for (BidDAO b: e.getListOfBids()){
                bids.add(b.toBid());
            }
            Collections.reverse(bids);
        }
        db.close();
        if (len == null || st == null){
            return gson.toJson(bids);
        }
        List<Bid> res = bids.subList(st, st + len);
        return gson.toJson(res);
    }




    /**
     *
     * @param id of the auction
     * @param input of the question string
     * @param session
     * @return
     */
    @Path("/{id}/question")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String questionCreate(@PathParam("id") String id,String input, @CookieParam("scc:session") Cookie session){
        CosmoDBLayer db = CosmoDBLayer.getInstance();
        CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(id);
        if(!res.iterator().hasNext()){
            return "There is no such action";
        }
        AuctionDAO auction = res.iterator().next();
        Gson gson=new Gson();
        QuestionDAO questionDAO = gson.fromJson(input,QuestionDAO.class);
        Integer index = QUESTION_ID.getAndIncrement();
        questionDAO.setId(index.toString());
        questionDAO.setReply(null);
        CosmosPagedIterable<UserDAO> result = db.getUserById(questionDAO.getUser());
        if(!result.iterator().hasNext()){
            return "There is no such user";
        }
        UserDAO user = result.iterator().next();
        try {
            checkCookieUser(session, user.getId());
        }
        catch( WebApplicationException e) {
            throw e;
        }
        try{
            auction.addQuestion(questionDAO);
        }
        catch(Exception e){
            auction.setListOfQuestions(new ArrayList<QuestionDAO>());
            auction.addQuestion(questionDAO);

        }
        db.updateAuction(auction);
        db.close();
        return gson.toJson(questionDAO);
    }




    /**
     *
     * @param auctionId the id of the auction
     * @param input json
     * @param questionID
     * @param session
     * @return
     */
    @Path("/{id}/question/{qid}/reply")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String questionAnswer(@PathParam("id") String auctionId,String input,@PathParam("qid") String questionID, @CookieParam("scc:session") Cookie session){
        // Getting proper auction
        CosmoDBLayer db = CosmoDBLayer.getInstance();
        CosmosPagedIterable<AuctionDAO> res = db.getAuctionById(auctionId);
        if(!res.iterator().hasNext()){
            return "There is no such auction";
        }
        AuctionDAO auction = res.iterator().next();

        // Getting reply
        Gson gson=new Gson();
        JsonElement element = gson.fromJson (input, JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();
        String reply = jsonObj.get("reply").getAsString();

        try {
            checkCookieUser(session, auction.getOwner());
        }
        catch( WebApplicationException e) {
            throw e;
        }
        for (QuestionDAO question : auction.getListOfQuestions()){
            if (question.getId().equals(questionID)){
                if(question.getReply() == null){
                    question.setReply(reply);
                    db.updateAuction(auction);
                    db.close();
                    return "You have successfully replied";
                }
                else{
                    return "The question was already answered";
                }
            }
        }
        return "There is no such question mate";
    }




    /**
     *
     * Gets all questions of some auction
     * @param id
     * @return
     */
    @Path("/{id}/question")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listQuestions(@PathParam("id")String id){
        Gson gson = new Gson();
        CosmoDBLayer db = CosmoDBLayer.getInstance();
        CosmosPagedIterable<AuctionDAO> resGet = db.getAuctionById(id);
        ArrayList<Question> questions = new ArrayList<>();
        try {
            AuctionDAO auction = resGet.iterator().next();
            for ( QuestionDAO question : auction.getListOfQuestions()){
                questions.add(question.toQuestion());
            }
            db.close();
            return gson.toJson(questions);
        }
        catch(Exception e){
            throw new ServiceUnavailableException();
        }
    }






    /**
     * @return list of auction that are about to close
     */
    @Path("/closing")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String listAuctionsAboutToClose(){
        Gson gson = new Gson();
        CosmoDBLayer db = CosmoDBLayer.getInstance();
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
            return gson.toJson(closingAuctions);
        }
        return gson.toJson(closingAuctions);
    }




    /**
     * Throws exception if not appropriate user for operation on Auction
     */
    public String checkCookieUser(Cookie session, String id)
            throws NotAuthorizedException {
        if (session == null || session.getValue() == null)
            throw new NotAuthorizedException("No session initialized");
        String s;
        try(Jedis jedis = RedisCache.getCachePool().getResource();) {
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





    /**
     * Merges two objects, used in update operation
     */
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

