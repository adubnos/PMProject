package com.example.pmproject.Controller;

import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.DTO.MemberUpdateDTO;
import com.example.pmproject.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping({"/member/info","/admin/info"})
    public String memberInfo(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDTO member = memberService.listOne(userDetails.getUsername());

        model.addAttribute("member", member);


        if("/admin/info".equals(RequestContextHolder.currentRequestAttributes().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST))) {
            return "admin/info";
        }else {
            return "member/info";
        }
    }

    @GetMapping({"/member/update","/admin/update"})
    public String updateForm(MemberUpdateDTO memberUpdateDTO, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDTO memberDTO=memberService.listOne(userDetails.getUsername());
        model.addAttribute("memberDTO", memberDTO);

        if("/admin/update".equals(RequestContextHolder.currentRequestAttributes().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST))) {
            return "admin/update";
        }else {
            return "member/update";
        }
    }

    @PostMapping("/member/update")
    public String memberUpdate(@Valid MemberUpdateDTO memberUpdateDTO, MemberDTO memberDTO, BindingResult bindingResult, Model model, Authentication authentication) {
        if(bindingResult.hasErrors()) {
            return "member/update";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            String result = memberService.update(memberUpdateDTO, userDetails.getUsername());
            if(result==null) {
                model.addAttribute("wrongPwd", "기존 비밀번호가 맞지 않습니다.");
                return "member/update";
            }
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "member/update";
        }

        return "redirect:/member/info";
    }

    @PostMapping("/admin/update")
    public String adminUpdate(@Valid MemberUpdateDTO memberUpdateDTO, MemberDTO memberDTO, BindingResult bindingResult, Model model, Authentication authentication) {
        if(bindingResult.hasErrors()) {
            return "admin/update";
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            String result = memberService.update(memberUpdateDTO, userDetails.getUsername());
            if(result==null) {
                model.addAttribute("wrongPwd", "기존 비밀번호가 맞지 않습니다.");
                return "admin/update";
            }
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            return "admin/update";
        }

        return "redirect:/admin/info";
    }

    @GetMapping({"/member/withdrawal", "/admin/withdrawal"})
    public String withdrawalForm() {
        if("/admin/withdrawal".equals(RequestContextHolder.currentRequestAttributes().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST))) {
            return "admin/withdrawal";
        }else {
            return "member/withdrawal";
        }
    }

    @PostMapping("/member/withdrawal")
    public String memberWithdrawal(@RequestParam String password, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean result = memberService.withdrawal(userDetails.getUsername(), password);

        if(result) {
            return "redirect:/logout";
        }else {
            model.addAttribute("wrongPwd", "비밀번호가 맞지 않습니다.");
            return "member/withdrawal";
        }
    }

    @PostMapping("/admin/withdrawal")
    public String adminWithdrawal(@RequestParam String password, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean result = memberService.withdrawal(userDetails.getUsername(), password);

        if(result) {
            return "redirect:/logout";
        }else {
            model.addAttribute("wrongPwd", "비밀번호가 맞지 않습니다.");
            return "admin/withdrawal";
        }
    }
}
