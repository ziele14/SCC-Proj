package scc.srv;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import jakarta.ws.rs.*;
import scc.utils.Hash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jakarta.ws.rs.core.MediaType;


/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource
{
	Map<String,byte[]> map = new HashMap<String,byte[]>();

	String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=nazwastorage;AccountKey=LsIrcQVWjQLBI6/whZaZbgMGlyNLynCcPnvqjrQDeIzELy+ZgxzsP7PW9I2hGSs71IaD2sXbyv8T+AStfYR2iQ==;EndpointSuffix=core.windows.net";

	BlobContainerClient containerClient = new BlobContainerClientBuilder()
			.connectionString(storageConnectionString)
			.containerName("images")
			.buildClient();

	/**
	 * Post a new image.The id of the image is its hash.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public String upload(byte[] contents) {
		String key = Hash.of(contents);
		map.put( key, contents);
		BlobClient blob = containerClient.getBlobClient(key);
		blob.upload(BinaryData.fromBytes(contents));
		return "The image has been added with this ID : " + key;
	}

	/**
	 * Return the contents of an image. Throw an appropriate error message if
	 * id does not exist.
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] download(@PathParam("id") String id) {
		try {
			BlobClient blob = containerClient.getBlobClient(id);
			BinaryData data = blob.downloadContent();
			byte[] arr = data.toBytes();
			return arr;
		}
		catch(Exception e) {
			throw new ServiceUnavailableException();
		}
	}

	/**
	 * Lists the ids of images stored.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String list() {
		PagedIterable<BlobItem> blob = containerClient.listBlobs();
		ArrayList<String> users = new ArrayList<String>();
		for (BlobItem bb: blob){
			String BName = bb.getName();
			users.add(BName);
		}
		if (users.size() == 0){
			return "The images list is unfortunately empty";
		}
		else {
			return users.toString();
		}
	}


	@DELETE
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String deletePhotos(){
		PagedIterable<BlobItem> blobs = containerClient.listBlobs();

		for (BlobItem bb : blobs){
			BlobClient blob = containerClient.getBlobClient(bb.getName());
			blob.delete();
		}
		return "jejku mój blob";
		}
}