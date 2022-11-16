//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package scc.data;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.models.PartitionKey;
import com.azure.cosmos.util.CosmosPagedIterable;
import io.micrometer.core.lang.Nullable;

public class CosmoDBLayer {
    private static final String CONNECTION_URL = "https://nazwa.documents.azure.com:443/";
    private static final String DB_KEY = "Svs6zM8EEmGrsKiNEhH5ZC30NbcuNpleTm1JHjHaMNFiKcbhw3vgSkNeTHgc8kW4qP7xOOkcMXoqaKMU68H76Q==";
    private static final String DB_NAME = "nazwadb";
    private static CosmoDBLayer instance;
    private CosmosClient client;
    private CosmosDatabase db;
    private CosmosContainer users;
    private CosmosContainer auctions;

    public static synchronized CosmoDBLayer getInstance() {
        if (instance != null) {
            return instance;
        } else {
            CosmosClient client = (new CosmosClientBuilder()).endpoint(CONNECTION_URL).key(DB_KEY).gatewayMode().consistencyLevel(ConsistencyLevel.SESSION).connectionSharingAcrossClientsEnabled(true).contentResponseOnWriteEnabled(true).buildClient();
            instance = new CosmoDBLayer(client);
            return instance;
        }
    }

    public CosmoDBLayer(CosmosClient client) {
        this.client = client;
    }

    private synchronized void init() {
        if (this.db == null) {
            this.db = this.client.getDatabase(DB_NAME);
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


    public CosmosItemResponse<AuctionDAO> updateAuction(AuctionDAO auctionDAO){
        this.init();
        PartitionKey key = new PartitionKey(auctionDAO.getId());
        return this.auctions.upsertItem(auctionDAO, key, new CosmosItemRequestOptions());
    }

    public CosmosItemResponse<UserDAO> updateUser(UserDAO userDAO){
        this.init();
        PartitionKey key = new PartitionKey(userDAO.getId());
        return this.users.upsertItem(userDAO, key, new CosmosItemRequestOptions());
    }


    public CosmosPagedIterable<UserDAO> getUserById(String id) {
        this.init();
        return this.users.queryItems("SELECT * FROM users WHERE users.id=\"" + id + "\"", new CosmosQueryRequestOptions(), UserDAO.class);
    }


    public CosmosPagedIterable<AuctionDAO> getAuctionById(String id) {
        this.init();
        return this.auctions.queryItems("SELECT * FROM auctions WHERE auctions.id=\"" + id + "\"", new CosmosQueryRequestOptions(), AuctionDAO.class);
    }

    public CosmosPagedIterable<AuctionDAO> getAuctionByOwnerId(String ownerId) {
        this.init();
        return this.auctions.queryItems("SELECT * FROM auctions WHERE auctions.ownerId=\"" + ownerId + "\"", new CosmosQueryRequestOptions(), AuctionDAO.class);
    }

    public CosmosPagedIterable<UserDAO> getUsers() {
        this.init();
        return this.users.queryItems("SELECT * FROM users ", new CosmosQueryRequestOptions(), UserDAO.class);
    }

    public CosmosPagedIterable<AuctionDAO> getRecentAuctions(@Nullable int num) {
        this.init();
        return this.auctions.queryItems("SELECT * FROM auctions LIMIT \"" +num + "\" ORDER BY auctions.id DESC ", new CosmosQueryRequestOptions(), AuctionDAO.class);
    }

    public CosmosPagedIterable<AuctionDAO> getAuctions(@Nullable String status) {
        this.init();
        if(status == null){
            return this.auctions.queryItems("SELECT * FROM auctions ", new CosmosQueryRequestOptions(), AuctionDAO.class);
        }
        return this.auctions.queryItems("SELECT * FROM auctions WHERE auctions.status=\"" + status + "\"", new CosmosQueryRequestOptions(), AuctionDAO.class);
    }


    public void close() {
        this.client.close();
    }
}
