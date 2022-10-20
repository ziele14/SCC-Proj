package scc.srv;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
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
        UserDAO userDAO = gson.fromJson(inpucik, UserDAO.class);
        userDAO.setPwd(Hash.of(userDAO.getPwd()));
        db.putUser(userDAO);
        db.close();
        return "User created : " + userDAO.getName() + " " + userDAO.getId();
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
            users.add(e.toString());
        }
        db.close();
        return users.toString();
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
            users.add(e.toString());
        }
        db.close();
        if (users.size() == 0) {
            return "there is no such user here :/";
        }
        return users.toString();
    }

    /**
     * deletes user
     */

    @Path("/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String wypierdalaj(@PathParam("id")String id){
        db.delUserById(id);
        db.close();
        return "User with id = " + id + " has been deleted";
    }

}
