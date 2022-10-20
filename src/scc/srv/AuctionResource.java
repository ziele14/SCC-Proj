package scc.srv;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import scc.data.AuctionDAO;
import scc.data.CosmoDBLayer;

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
        return "udało się stworzyć aukcję chuj w dupie chlupie co się dzieje w tej chałupie"+auctionDAO.getTitle();
    }


}

