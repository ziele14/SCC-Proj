package scc.srv;

import com.azure.core.annotation.Put;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import scc.data.AuctionDAO;
import scc.data.BidDAO;
import scc.data.CosmoDBLayer;
import scc.data.UserDAO;
import scc.utils.Hash;

import java.util.ArrayList;

/**
 * Resource for managing users.
 */
@Path("/user")
public class UserResource {

    CosmoDBLayer db = CosmoDBLayer.getInstance();

    /**
     * creates a user from a json file and adds it to our cosmoDB database
     */
    @Path("/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String object_create(String inpucik){
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
    public String object_get(){
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
    public String object_get_id(@PathParam("id") String id){
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
    public String delete_user(@PathParam("id")String id){
            CosmosPagedIterable<AuctionDAO> result = db.getAuctionByOwnerId(id);
            for( AuctionDAO auction : result){
                    auction.setOwnerId("Deleted user");
                    db.delAuctionById(auction.getId());
                    db.putAuction(auction);
                }
        CosmosPagedIterable<BidDAO> results = db.getBidsByUserID(id);
            for( BidDAO bid : results){
                db.delBid(bid);
                bid.setUserId("Deleted user");
                db.putBid(bid);
            }
        try {
            db.delUserById(id);
            db.close();
        }
        catch(Exception e){
            return "There is no such user in our database";
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
    public String update_user(@PathParam("id")String id, String inpucik){
        Gson gson = new Gson();
        try {
            UserDAO userDAO = gson.fromJson(inpucik, UserDAO.class);
            userDAO.setPwd(Hash.of(userDAO.getPwd()));
            db.delUserById(id);
            db.putUser(userDAO);
            db.close();
            return "User updated, new values : " + userDAO.toUser().toString();
        }
        catch(Exception e){
            return "There is no user with this ID or the data has invalid form";
        }

    }
    @Path("/")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String delete_users(){
        CosmosPagedIterable<UserDAO> users = db.getUsers();
        for(UserDAO user : users){
            db.delUser(user);
        }
        return "brawo głuptasie wszystko usunąłeś";
    }

}
