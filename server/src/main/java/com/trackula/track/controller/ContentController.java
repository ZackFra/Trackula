package com.trackula.track.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ContentController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
