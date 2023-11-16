package com.example.pmproject.Controller;

import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.DTO.MemberPasswordDTO;
import com.example.pmproject.Service.GlobalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class GlobalController {

    private final GlobalService globalService;

    @GetMapping("/")
    public String main() {
        return "pmmain1";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "loginForm";
    }

    @GetMapping("/register")
    public String registerForm(MemberDTO memberDTO) {
        return "member/register";
    }

    @PostMapping("/register")
    public String register(@Valid MemberDTO memberDTO, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            return "member/register";
        }
        try {
            globalService.register(memberDTO);
            return "redirect:/";
        }catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "member/register";
        }
    }

    @GetMapping("/findPassword")
    public String findPasswordForm(MemberPasswordDTO memberPasswordDTO) {
        return "member/findPassword";
    }

    @PostMapping("/findPassword")
    public String findPassword(@Valid MemberPasswordDTO memberPasswordDTO, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            return "member/findPassword";
        }
        try {
            globalService.findPassword(memberPasswordDTO);
            return "redirect:/";
        }catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "member/findPassword";
        }
    }
}
