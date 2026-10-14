package com.example.controllerendpoints;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class PageController {

    @GetMapping("/index")
    public String index() {
        return "This is the main page";
    }

    @GetMapping("/contact")
    public String contact() {
        return "This is the contact page";
    }
}
