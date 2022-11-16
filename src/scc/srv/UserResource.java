package scc.srv;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.data.*;
import scc.utils.Hash;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

/**
 * Resource for managing users.
 */
@Path("/user")
public class UserResource {

    CosmoDBLayer db = CosmoDBLayer.getInstance();
    Jedis jedis = RedisCache.getCachePool().getResource();

    /**
     * creates a user from a json file and adds it to our cosmoDB database
     */
    @Path("/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String userCreate(String inpucik){
        Gson gson = new Gson();
        try {
            UserDAO userDAO = gson.fromJson(inpucik, UserDAO.class);
            userDAO.setPwd(Hash.of(userDAO.getPwd()));
            db.putUser(userDAO);
            db.close();
            String nitka="User created, name : " + userDAO.getName() + ", ID : " + userDAO.getId();

            return gson.toJson(userDAO);
        }
        catch(Exception e){
            return "The input user data seems to be invalid or the ID is already taken";
        }
    }

    /**
     * returns all users
     */
    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllUsers(){
        CosmosPagedIterable<UserDAO> resGet = db.getUsers();
        ArrayList<String> users = new ArrayList<String>();
        for( UserDAO e: resGet) {
            users.add(e.toUser().toString());
        }
        db.close();
        if(users.size() == 0){
            return "It seems there are no users in the database";
        }
        else {
            return users.toString();
        }
    }


    /**
     * returns user by id
     */
    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String userGetById(@PathParam("id") String id){
        CosmosPagedIterable<UserDAO> res = db.getUserById(id);
        ArrayList<String> users = new ArrayList<String>();
        for( UserDAO e: res) {
            users.add(e.toUser().toString());
        }
        db.close();
        if (users.size() == 0) {
            return "There is no such user here :/";
        }
        return users.get(0).toString();
    }


    /**
     * deletes user
     */
    @Path("/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String deleteUser(@PathParam("id")String id, @CookieParam("scc:session") Cookie session){
        try {
            checkCookieUser(session, id);
            db.delUserById(id);
            db.close();
        }
        catch( WebApplicationException e) {
            throw e;
        }
        catch(Exception e){
            return "There is no such user in our database";
        }
            CosmosPagedIterable<AuctionDAO> auctions = db.getAuctions(null);
            for(AuctionDAO auction : auctions){
                if(Objects.equals(auction.getOwnerId(),id)){
                    auction.setOwnerId("Deleted user");
                    for(BidDAO bid : auction.getListOfBids()) {
                        if(Objects.equals(bid.getUserId(),id)){
                            auction.getListOfBids().remove(bid);
                            bid.setUserId("Deleted user");
                            bid.setId(bid.getUserId() + " : " + bid.getBid_value());
                            auction.getListOfBids().add(bid);
                        }
                    }
                }
                db.updateAuction(auction);
            }
        return "User with id = " + id + " has been deleted";


    }


    /**
     * changes user
     */
    @Path("/{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String updateUser(@PathParam("id")String id, String inpucik, @CookieParam("scc:session") Cookie session){
        Gson gson = new Gson();
        try {
            UserDAO userDAO = gson.fromJson(inpucik, UserDAO.class);
            userDAO.setPwd(Hash.of(userDAO.getPwd()));
            checkCookieUser(session, id);
            db.updateUser(userDAO);
            db.close();
            return "User updated, new values : " + userDAO.toUser().toString();
        }
        catch( WebApplicationException e) {
            throw e;
        }
        catch(Exception e){
            return "There is no user with this ID or the data has invalid form";
        }

    }

    @Path("/{id}/auctions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUsersAuctions(@PathParam("id")String id, @QueryParam("status") String status){
        CosmosPagedIterable<AuctionDAO> result = db.getAuctions(status);
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        for( AuctionDAO e: result) {
            if (Objects.equals(e.getOwnerId(),id)){
                auctions.add(e.toAuction());
            }
        }
        db.close();
        return auctions.toString();

    }

    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response auth(String input) {
        boolean pwdOk = false;

        Gson gson = new Gson();
        UserDAO userDAO = gson.fromJson(input, UserDAO.class);
        userDAO.setPwd(Hash.of(userDAO.getPwd()));

        CosmosPagedIterable<UserDAO> user = db.getUserById(userDAO.getId());
        for( UserDAO e: user) {
            if (Objects.equals(e.getPwd(),userDAO.getPwd())){
                pwdOk = true;
            }
        }
        if(pwdOk) {
            String uid = UUID.randomUUID().toString();
            NewCookie cookie = new NewCookie.Builder("scc:session")
                    .value(uid)
                    .path("/")
                    .comment("sessionid")
                    .maxAge(3600)
                    .secure(false)
                    .httpOnly(true)
                    .build();
            jedis.set(uid, userDAO.getId());
            return Response.ok().cookie(cookie).build();
        } else
            throw new NotAuthorizedException("Incorrect login");
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
