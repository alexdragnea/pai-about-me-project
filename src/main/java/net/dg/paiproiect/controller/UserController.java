package net.dg.paiproiect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @GetMapping("/courses")
    public String courses() {
        return "courses";
    }
}
