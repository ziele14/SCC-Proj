package scc.srv;

import com.azure.core.http.rest.PagedIterable;
import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import jakarta.ws.rs.*;
import redis.clients.jedis.Jedis;
import scc.cache.RedisCache;
import scc.utils.Hash;

import java.util.*;

import jakarta.ws.rs.core.MediaType;


/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource
{

	String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=nazwastorage;AccountKey=Z8tKUgWJJDoaSr7/XLFsOXIHJhVKU7YUepYsv/sZRFoM9IX+x+SG3C4JGwtxY6LGaLocpnHJ52mb+AStoHLDEQ==;EndpointSuffix=core.windows.net";

	BlobContainerClient containerClient = new BlobContainerClientBuilder()
			.connectionString(storageConnectionString)
			.containerName("images")
			.buildClient();

	Jedis jedis = RedisCache.getCachePool().getResource();

	/**
	 * Post a new image.The id of the image is its hash.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public String upload(byte[] contents) {
		String key = Hash.of(contents);
		BlobClient blob = containerClient.getBlobClient(key);
		blob.upload(BinaryData.fromBytes(contents));
		/** cache tutaj wlatuje, mati*/
		jedis.set("image: " + key, Base64.getEncoder().encodeToString(contents));
		jedis.expire(key,86400);
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
			if (jedis.get("image: " + id) instanceof String) {
				byte [] arr = Base64.getDecoder().decode(jedis.get("image: " + id));
				return arr;
			}
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
		ArrayList<String> pictures = new ArrayList<String>();
		for (BlobItem bb: blob){
			String BName = bb.getName();
			pictures.add(BName);
		}
		if (pictures.size() == 0){
			return "The images list is unfortunately empty";
		}
		else {
			return pictures.toString();
		}
	}


}