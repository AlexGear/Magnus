package ru.eltex.magnus.server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    @PostMapping("/login")
    public String submit(@RequestParam("login") String login, @RequestParam("password") String password) {
        System.out.printf("Login: %s, Password: %s%n", login, password);
        return "redirect:/somepage.html";
    }
}
