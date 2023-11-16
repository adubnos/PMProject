package com.example.pmproject.Controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pm")
public class PmController {

    @GetMapping("/list")
    public String list() {
        return "pm/list";
    }
}
