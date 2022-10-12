package com.litserver.global.common;

import com.litserver.global.exception.apierror.ApiError;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class ResponseHandler {
    private boolean success;
    private Object data;
    private ApiError errorDetails;

    public static ResponseEntity<Object> ok(Object data) {
        return ResponseEntity.ok(ResponseHandler.builder()
                .success(true)
                .data(data)
                .errorDetails(null)
                .build());
    }

    public static ResponseEntity<Object> fail(ApiError apiError) {
        return new ResponseEntity<>(ResponseHandler.builder()
                .success(false)
                .data(null)
                .errorDetails(apiError)
                .build(), apiError.getStatus());
    }
}