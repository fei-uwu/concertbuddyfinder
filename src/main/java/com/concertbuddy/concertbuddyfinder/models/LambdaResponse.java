package com.concertbuddy.concertbuddyfinder.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LambdaResponse {
    private String message;
    private int httpCode;
}
