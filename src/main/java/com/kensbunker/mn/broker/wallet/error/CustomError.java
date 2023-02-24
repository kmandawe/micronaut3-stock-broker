package com.kensbunker.mn.broker.wallet.error;

import com.kensbunker.mn.broker.wallet.api.RestApiResponse;

public record CustomError(
        int status,
        String error,
        String message
) implements RestApiResponse {
}
