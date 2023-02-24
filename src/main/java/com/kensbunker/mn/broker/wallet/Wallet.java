package com.kensbunker.mn.broker.wallet;

import com.kensbunker.mn.broker.Symbol;

import java.math.BigDecimal;
import java.util.UUID;

public record Wallet(UUID accountId, UUID walletId, Symbol symbol, BigDecimal available, BigDecimal locked) {
    
}
