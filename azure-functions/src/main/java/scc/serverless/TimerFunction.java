package scc.serverless;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import com.azure.cosmos.util.CosmosPagedIterable;
import com.microsoft.azure.functions.annotation.*;

import com.microsoft.azure.functions.*;
import scc.data.AuctionDAO;
import scc.data.CosmoDBLayer;

/**
 * Azure Functions with Timer Trigger.
 */
public class TimerFunction {
    @FunctionName("auction-closing")
    public void cosmosFunction( @TimerTrigger(name = "timer",
    								schedule = "* * * * *")
    				String timerInfo,
    				ExecutionContext context) {

		context.getLogger().info("Java Timer trigger function executed at: " + LocalDateTime.now());
		CosmosPagedIterable<AuctionDAO> auctions = CosmoDBLayer.getInstance().getAuctions();
		CosmoDBLayer db = CosmoDBLayer.getInstance();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		for (AuctionDAO auction: auctions){
			LocalDateTime auctionTime = LocalDateTime.parse(auction.getEndTime(), formatter);
			if (auctionTime.isBefore(LocalDateTime.now())){
				auction.AuctionClose();
				db.updateAuction(auction);
//				db.delAuctionById(auction.getId());
//				db.putAuction(auction);
//				db.close();
				context.getLogger().info("Auction " + auction.toString() + "has been closed\n\n");
			}
		}
//		try (Jedis jedis = RedisCache.getCachePool().getResource()) {
//			jedis.incr("cnt:timer");
//			jedis.set("serverless-time", new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(new Date()));
//		}
    }
}
