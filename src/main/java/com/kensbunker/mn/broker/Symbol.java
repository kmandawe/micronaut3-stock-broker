package com.kensbunker.mn.broker;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Symbol", description = "Abbreviation to uniquely identify public trades shares of a stock")
public record Symbol(@Schema(description = "symbol value", minLength = 1, maxLength = 5) String value) {
    
}
