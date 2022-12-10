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

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.ws.rs.core.MediaType;


/**
 * Resource for managing media files, such as images.
 */
@Path("/media")
public class MediaResource
{

//	String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=nazwadb;AccountKey=dlQ9N9X0PZI201Lykzv8Q1aEOiPP49L6G2z+A51k8Qyya5TwnK+1gbMISVHAAcn8/Qu4CA2Sru/O+AStzldsjQ==;EndpointSuffix=core.windows.net";



	private static AtomicInteger ADDITIONAL = new AtomicInteger(1);
	/**
	 * Post a new image.The id of the image is its hash.
	 */
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_JSON)
	public String upload(byte[] contents) {
		String key = Hash.of(contents);

		FileOutputStream out = null;
		try {
			out = new FileOutputStream("/mnt/vol/"+key);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		try {
			out.write(contents);
			out.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}


//		BlobContainerClient containerClient = new BlobContainerClientBuilder()
//				.connectionString(storageConnectionString)
//				.containerName("images")
//				.buildClient();

//		if(containerClient.getBlobClient(key) instanceof com.azure.storage.blob.BlobClient){
//			key = key + ADDITIONAL.getAndIncrement();
//		}
//		BlobClient blob = containerClient.getBlobClient(key);
//		blob.upload(BinaryData.fromBytes(contents));

		/** cache tutaj wlatuje, mati*/
		try(Jedis jedis = RedisCache.getCachePool().getResource()){
			jedis.set("image: " + key, Base64.getEncoder().encodeToString(contents));
			jedis.expire(key, 86400);

		}
		catch(Exception e){
			throw new ServiceUnavailableException();
		}
		return key;
	}

	/**
	 * Return the contents of an image. Throw an appropriate error message if
	 * id does not exist.
	 */
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public byte[] download(@PathParam("id") String id) {
//		BlobContainerClient containerClient = new BlobContainerClientBuilder()
//				.connectionString(storageConnectionString)
//				.containerName("images")
//				.buildClient();

		try (Jedis jedis = RedisCache.getCachePool().getResource();){
			if (jedis.get("image: " + id) instanceof String) {
				byte [] arr = Base64.getDecoder().decode(jedis.get("image: " + id));
				return arr;
			}

//			BlobClient blob = containerClient.getBlobClient(id);
//			BinaryData data = blob.downloadContent();
//			byte[] arr = data.toBytes();
//			return arr;
			try {
				// Open the file using the file path
				FileInputStream fileInputStream = new FileInputStream("/mnt/vol/"+id);

				// Create a byte array to hold the contents of the file
				byte[] data = new byte[fileInputStream.available()];

				// Read the file into the byte array
				fileInputStream.read(data);


				return data;
			} catch (IOException e) {
				// Handle the exception
			}

		}
		catch(Exception e) {
			throw new ServiceUnavailableException();
		}
		return null ;
	}

	/**
	 * Lists the ids of images stored.
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public String list() {
		String dir = "/mnt/vol/";

		// Create a File object for the directory
		File directory = new File(dir);

		// Get all the files in the directory
		File[] files = directory.listFiles();

		// Loop through the files and print the names of the binary files
		ArrayList<String> pictures = new ArrayList<String>();
		for (File file : files) {
			pictures.add(file.getName());

		}
		if (pictures.size() == 0){
			return "The images list is unfortunately empty";
		}
		else {
			return pictures.toString();
	}
//		BlobContainerClient containerClient = new BlobContainerClientBuilder()
//				.connectionString(storageConnectionString)
//				.containerName("images")
//				.buildClient();
//		PagedIterable<BlobItem> blob = containerClient.listBlobs();
//		ArrayList<String> pictures = new ArrayList<String>();
//		for (BlobItem bb: blob){
//			String BName = bb.getName();
//			pictures.add(BName);
//		}


}
}