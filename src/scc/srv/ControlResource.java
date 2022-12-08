package scc.srv;

import com.azure.core.http.rest.PagedIterable;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import scc.data.AuctionDAO;
import scc.data.CosmoDBLayer;
import scc.data.UserDAO;

import javax.print.attribute.standard.Media;

/**
 * Class with control endpoints.
 */
@Path("/ctrl")
public class ControlResource
{

	/**
	 * This methods just prints a string. It may be useful to check if the current 
	 * version is running on Azure.
	 */
	@Path("/version")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hello() {
		return "v: 0001";
	}

	/**
	 * This methods deletes all Users and Media items from the database
	 */
	@Path("/")
	@DELETE
	@Produces(MediaType.TEXT_PLAIN)
	public String cleanUp(){
		CosmoDBLayer db = CosmoDBLayer.getInstance();
		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.connectionString("DefaultEndpointsProtocol=https;AccountName=nazwastorage;AccountKey=Z8tKUgWJJDoaSr7/XLFsOXIHJhVKU7YUepYsv/sZRFoM9IX+x+SG3C4JGwtxY6LGaLocpnHJ52mb+AStoHLDEQ==;EndpointSuffix=core.windows.net")
				.containerName("images")
				.buildClient();

		CosmosPagedIterable<UserDAO> users = db.getUsers();
		for(UserDAO user : users){
			db.delUser(user);
		}

		PagedIterable<BlobItem> blobs = containerClient.listBlobs();
		for (BlobItem bb : blobs){
			BlobClient blob = containerClient.getBlobClient(bb.getName());
			blob.delete();
		}

		CosmosPagedIterable<AuctionDAO> auctions = db.getAuctions(null);
		for(AuctionDAO auction : auctions){
			db.delAuctions(auction);
		}

		return "No taki clean up, nie?";
	}
}
