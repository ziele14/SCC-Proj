package scc.srv;

import com.azure.core.http.rest.PagedIterable;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import scc.data.AuctionDAO;
import scc.data.CosmoDBLayer;
import scc.data.UserDAO;

import javax.print.attribute.standard.Media;
import java.io.File;

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
//		BlobContainerClient containerClient = new BlobContainerClientBuilder()
//				.connectionString("DefaultEndpointsProtocol=https;AccountName=nazwadb;AccountKey=dlQ9N9X0PZI201Lykzv8Q1aEOiPP49L6G2z+A51k8Qyya5TwnK+1gbMISVHAAcn8/Qu4CA2Sru/O+AStzldsjQ==;EndpointSuffix=core.windows.net")
//				.containerName("images")
//				.buildClient();

		CosmosPagedIterable<UserDAO> users = db.getUsers();
		for(UserDAO user : users){
			db.delUser(user);
		}

//		PagedIterable<BlobItem> blobs = containerClient.listBlobs();
//		for (BlobItem bb : blobs){
//			BlobClient blob = containerClient.getBlobClient(bb.getName());
//			blob.delete();
//		}

		// Specify the directory where the files are located
		String dir = "/mnt/vol/";

		// Create a File object for the directory
		File directory = new File(dir);

		// Get all the files in the directory
		File[] files = directory.listFiles();

		// Loop through the files and delete them
		for (File file : files) {
			if (file.isFile()) {
				file.delete();
			}
		}

		CosmosPagedIterable<AuctionDAO> auctions = db.getAuctions(null);
		for(AuctionDAO auction : auctions){
			db.delAuctions(auction);
		}

		return "No taki clean up, nie?";
	}
}
