package com.lewis.mongo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by liu on 2017/6/7.
 */
@Controller
public class MongoController {
    //@RequestMapping(value = "hello", method = RequestMethod.GET)
    @RequestMapping("/hi/hello")
    public String printHello() {
        System.out.println("hello");
        return "mongoList";
    }
}
