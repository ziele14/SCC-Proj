package scc.srv;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import scc.data.AuctionDAO;
import scc.data.BidDAO;
import scc.data.CosmoDBLayer;
import scc.data.UserDAO;

import java.util.ArrayList;

@Path("/auction")
public class AuctionResource {
    CosmoDBLayer db = CosmoDBLayer.getInstance();

    @Path("/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String object_create(String input){
        Gson gson=new Gson();
        AuctionDAO auctionDAO=gson.fromJson(input,AuctionDAO.class);
        db.putAuction(auctionDAO);
        db.close();
        return "udało się stworzyć aukcję "+auctionDAO.getTitle();
    }

    @Path("{id}/bid")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String bid_create(String input){
        Gson gson=new Gson();
        BidDAO bidDAO=gson.fromJson(input,BidDAO.class);
        db.putBid(bidDAO);
        db.close();
        return "stworzyłeś bida gratulacje";
    }
    @Path("{id}/bid")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String list_bids(@PathParam("id") String id){
        CosmosPagedIterable<BidDAO> resGet=db.getBids(id);
        ArrayList<String> bids = new ArrayList<String>();
        for( BidDAO e: resGet) {
            bids.add(e.toString());
        }
        db.close();
        return bids.toString();


    }



}

