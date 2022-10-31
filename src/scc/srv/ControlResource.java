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
				.connectionString("DefaultEndpointsProtocol=https;AccountName=nazwastorage;AccountKey=LsIrcQVWjQLBI6/whZaZbgMGlyNLynCcPnvqjrQDeIzELy+ZgxzsP7PW9I2hGSs71IaD2sXbyv8T+AStfYR2iQ==;EndpointSuffix=core.windows.net")
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

		return "No taki clean up, nie?";
	}
}
