//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package scc.data;

import com.azure.cosmos.ConsistencyLevel;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;

public class CosmoDBLayer {
    private static final String CONNECTION_URL = "https://antolomanolo.documents.azure.com:443/";
    private static final String DB_KEY = "K2ob1pvW5vG1nAf551fIDRp5QtC0Qg3TBp24XHxwiwN5iOY596c9oWn9c2RZNUrw4EYOn3i5IajPuvmNqZEH5w==";
    private static final String DB_NAME = "scc148287db";
    private static CosmoDBLayer instance;
    private CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer users;
    private CosmosContainer auctions;

    public static synchronized CosmoDBLayer getInstance() {
        if (instance != null) {
            return instance;
        } else {
            CosmosClient client = (new CosmosClientBuilder()).endpoint("https://antolomanolo.documents.azure.com:443/").key("K2ob1pvW5vG1nAf551fIDRp5QtC0Qg3TBp24XHxwiwN5iOY596c9oWn9c2RZNUrw4EYOn3i5IajPuvmNqZEH5w==").gatewayMode().consistencyLevel(ConsistencyLevel.SESSION).connectionSharingAcrossClientsEnabled(true).contentResponseOnWriteEnabled(true).buildClient();
            instance = new CosmoDBLayer(client);
            return instance;
        }
    }

    public CosmoDBLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if (this.db == null) {
            this.db = this.client.getDatabase("scc148287db");
            this.users = this.db.getContainer("users");
            this.auctions = this.db.getContainer("auctions");

        }
    }

    public CosmosItemResponse<Object> delUserById(String id) {
        this.init();
        PartitionKey key = new PartitionKey(id);
        return this.users.deleteItem(id, key, new CosmosItemRequestOptions());
    }
    public CosmosItemResponse<Object> delAuctionById(String id) {
        this.init();
        PartitionKey key = new PartitionKey(id);
        return this.auctions.deleteItem(id, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<Object> delUser(UserDAO user) {
        this.init();
        return this.users.deleteItem(user, new CosmosItemRequestOptions());
    }
    public CosmosItemResponse<Object> delAuctions(AuctionDAO auction) {
        this.init();
        return this.auctions.deleteItem(auction, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<UserDAO> putUser(UserDAO user) {
        this.init();
        return this.users.createItem(user);
    }
    public CosmosItemResponse<AuctionDAO> putAuction(AuctionDAO auction) {
        this.init();
        return this.auctions.createItem(auction);
    }

    public CosmosPagedIterable<UserDAO> getUserById(String id) {
        this.init();
        return this.users.queryItems("SELECT * FROM users WHERE users.id=\"" + id + "\"", new CosmosQueryRequestOptions(), UserDAO.class);
    }
    public CosmosPagedIterable<AuctionDAO> getAuctionById(String id) {
        this.init();
        return this.auctions.queryItems("SELECT * FROM auctions WHERE auctions.id=\"" + id + "\"", new CosmosQueryRequestOptions(), AuctionDAO.class);
    }

    public CosmosPagedIterable<UserDAO> getUsers() {
        this.init();
        return this.users.queryItems("SELECT * FROM users ", new CosmosQueryRequestOptions(), UserDAO.class);
    }
    public CosmosPagedIterable<AuctionDAO> getAuctions() {
        this.init();
        return this.auctions.queryItems("SELECT * FROM auctions ", new CosmosQueryRequestOptions(), AuctionDAO.class);
    }

    public void close() {
        this.client.close();
    }
}
