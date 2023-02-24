package com.kensbunker.mn.broker.watchlist;

import com.kensbunker.mn.broker.Symbol;

import java.util.ArrayList;
import java.util.List;

public record WatchList(List<Symbol> symbols) {
    public WatchList() {
        this(new ArrayList<>());
    }
}
