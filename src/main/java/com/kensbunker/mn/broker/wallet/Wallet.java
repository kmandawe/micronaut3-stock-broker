package com.kensbunker.mn.broker.wallet;

import com.kensbunker.mn.broker.Symbol;
import com.kensbunker.mn.broker.wallet.api.RestApiResponse;

import java.math.BigDecimal;
import java.util.UUID;

public record Wallet(
        UUID accountId, 
        UUID walletId, 
        Symbol symbol,
        BigDecimal available,
        BigDecimal locked
) implements RestApiResponse {
    
    public Wallet addAvailable(BigDecimal amountToAdd) {
        return new Wallet(
                this.accountId,
                this.walletId,
                this.symbol,
                this.available.add(amountToAdd),
                this.locked
        );
    }
    
}
