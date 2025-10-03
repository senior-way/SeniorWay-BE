package com.seniorway.seniorway.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String redirectToTest() {
        return "redirect:/api/test/ping";
    }
}