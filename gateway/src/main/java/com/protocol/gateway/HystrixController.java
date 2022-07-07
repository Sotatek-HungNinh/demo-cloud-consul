package com.protocol.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class HystrixController {
    @GetMapping("/student")
    public String firstServiceFallback(){
        return "This is a fallback for first service.";
    }

    @GetMapping("/second")
    public String secondServiceFallback(){
        return "Second Server overloaded! Please try after some time.";
    }
}
