package com.example.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonController {

    @GetMapping("/access-denied")
    public String accessDeniedPage() {
        return "error/access_denied";
    }

}