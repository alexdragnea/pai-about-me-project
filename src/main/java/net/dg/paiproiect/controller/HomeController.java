package net.dg.paiproiect.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    String index() {
        return "home";
    }

    @GetMapping("/map")
    String inde2() {
        return "map";
    }
}
