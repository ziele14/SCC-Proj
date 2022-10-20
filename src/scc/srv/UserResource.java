package scc.srv;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import scc.data.CosmoDBLayer;
import scc.data.UserDAO;

import java.util.ArrayList;

/**
 * Resource for managing users.
 */
@Path("/user")
public class UserResource {


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
        CosmoDBLayer db = CosmoDBLayer.getInstance();
        db.putUser(userDAO);
        db.close();
        return "udało się : " + userDAO.getName() + " " + userDAO.getId();
    }

    /**
     * zwraca wszystkich userów
     */

    @Path("/")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String object_get(){
        CosmoDBLayer db = CosmoDBLayer.getInstance();
        CosmosPagedIterable<UserDAO> resGet = db.getUsers();
        ArrayList<String> users = new ArrayList<String>();
        for( UserDAO e: resGet) {
            users.add(e.toString());
        }
        db.close();
        return users.toString();
    }


    /**
     * zwraca usera po ID
     */

    @Path("/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String object_get_id(@PathParam("id") String id){
        CosmoDBLayer db = CosmoDBLayer.getInstance();
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
     * wypierdala usera
     */

    @Path("/{id}")
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public String wypierdalaj(@PathParam("id")String id){
        CosmoDBLayer db = CosmoDBLayer.getInstance();
        db.delUserById(id);
        return "User with id = " + id + " has been deleted";
    }

}
