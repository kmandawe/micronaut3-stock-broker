package com.kensbunker.mn.broker.watchlist;

import com.kensbunker.mn.broker.data.InMemoryAccountStore;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;

import java.util.UUID;

@Controller("/account/watchlist")
public record WatchListController(InMemoryAccountStore store) {
    
    static final UUID ACCOUNT_ID = UUID.randomUUID();
    
    @Get(produces = MediaType.APPLICATION_JSON)
    public WatchList get() {
        return store.getWatchList(ACCOUNT_ID);
    }
    
    @Put(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public WatchList update(@Body WatchList watchList) {
        return store.updateWatchList(ACCOUNT_ID, watchList);
    }
    
    @Status(HttpStatus.NO_CONTENT)
    @Delete(produces = MediaType.APPLICATION_JSON)
    public void delete() {
        store.deleteWatchList(ACCOUNT_ID);
    }
}
