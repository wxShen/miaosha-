package com.example.moocmiaosha.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestHelloController {

    @RequestMapping("/")
    public String test(){
        return "Hello world";
    }
}
