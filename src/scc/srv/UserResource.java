package scc.srv;

import com.azure.core.annotation.Put;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import scc.data.*;
import scc.utils.Hash;

import java.util.ArrayList;
import java.util.Objects;

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
    public String deleteUser(@PathParam("id")String id){
        try {
            db.delUserById(id);
            db.close();
        }
        catch(Exception e){
            return "There is no such user in our database";
        }
            CosmosPagedIterable<AuctionDAO> auctions = db.getAuctions();
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
    public String updateUser(@PathParam("id")String id, String inpucik){
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

    @Path("/{id}/auctions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getUsersAuctions(@PathParam("id")String id){
        CosmosPagedIterable<AuctionDAO> result = db.getAuctions();
        ArrayList<Auction> auctions = new ArrayList<Auction>();
        for( AuctionDAO e: result) {
            if (Objects.equals(e.getOwnerId(),id)){
                auctions.add(e.toAuction());
            }
        }
        db.close();
        return auctions.toString();

    }

}
