package ru.eltex.magnus.server.controllers;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;


@RestController
public class SecurityController {

    @RequestMapping(value = "/username", method = RequestMethod.GET)
    public String currentUserName(Principal principal) {
        return principal.getName();
    }

    @RequestMapping(value = "/user_authorities", method = RequestMethod.GET)
    public ArrayList<String> getUserAuthorities() {
        ArrayList<String> data = new ArrayList<>();
        for (GrantedAuthority authority: SecurityContextHolder.getContext().getAuthentication().getAuthorities()) {
            data.add(authority.getAuthority());
        }
        return data;
    }
}