package com.litserver;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sample")
public class SampleApi {

    @RequestMapping("/health")
    public String health() {
        return "OK";
    }
}
