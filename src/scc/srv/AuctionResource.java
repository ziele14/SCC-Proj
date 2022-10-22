package scc.srv;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.annotation.JsonValue;
import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import scc.data.AuctionDAO;
import scc.data.BidDAO;
import scc.data.CosmoDBLayer;
import scc.data.UserDAO;
import scc.utils.Hash;

import java.util.ArrayList;

@Path("/auction")
public class AuctionResource {
    CosmoDBLayer db = CosmoDBLayer.getInstance();

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




    @Path("{id}/bid")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public String bid_create(String input){
        Gson gson=new Gson();
        BidDAO bidDAO=gson.fromJson(input,BidDAO.class);
        db.putBid(bidDAO);
        db.close();
        return "You have created a bid ";
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

