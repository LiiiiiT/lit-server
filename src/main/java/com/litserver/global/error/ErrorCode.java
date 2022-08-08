package com.litserver.global.error;

import lombok.Data;

@Data
public class ErrorCode {
    private String name;
    private String message;

    public String name() {
        return this.name;
    }
    public String message() {
        return this.message;
    }
}
