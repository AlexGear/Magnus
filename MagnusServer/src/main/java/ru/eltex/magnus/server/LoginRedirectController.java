package ru.eltex.magnus.server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

//@Controller
public class LoginRedirectController {
    /*@RequestMapping("/{url:\\b(?!index.html)\\b\\S+}")
    public String redirectIfNotLoggedIn(@PathVariable String url) {
        System.out.println("URL: " + url);
        return "redirect:/";
    }*/
}
