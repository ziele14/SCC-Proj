package scc.serverless;

import java.time.*;
import java.time.format.DateTimeFormatter;

import com.azure.cosmos.util.CosmosPagedIterable;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import scc.data.AuctionDAO;
import scc.data.CosmoDBLayer;

/**
 * Azure Functions with Timer trigger.
 */
public class CloseAuctions {
    /**
     * This function will be invoked periodically according to the specified schedule.
     */
    @FunctionName("findAboutToClose")
    public void run(
        @TimerTrigger(name = "timerInfo", schedule = "* */1****") String timerInfo,
        final ExecutionContext context
    ) {
        CosmosPagedIterable<AuctionDAO> auctions = CosmoDBLayer.getInstance().getAuctions();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (AuctionDAO auction: auctions){
            LocalDateTime auctionTime = LocalDateTime.parse(auction.getEndTime(), formatter);
            if (auctionTime.isBefore(LocalDateTime.now())){
                auction.AuctionClose();
                context.getLogger().info("Java Timer trigger function executed at: " + LocalDateTime.now() + "\n Auction " + auction.toString() + "has been closed\n\n");
            }
        }

    }
}
