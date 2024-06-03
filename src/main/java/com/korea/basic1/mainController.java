package com.korea.basic1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class mainController {

    @GetMapping("/")
    public String root(Authentication authentication){

        System.out.println("hihi");
        System.out.println(authentication);
        return "main";
    }
}
