package com.example.pmproject.Controller;

import com.example.pmproject.DTO.AskCommentDTO;
import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.Service.AskCommentService;
import com.example.pmproject.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/ask/comment")
public class AskCommentController {

    private final AskCommentService askCommentService;
    private final MemberService memberService;

    @PostMapping("/register")
    public String register(Long askId, AskCommentDTO askCommentDTO, Authentication authentication, RedirectAttributes redirectAttributes) {
        UserDetails userDetails=(UserDetails) authentication.getPrincipal();
        MemberDTO memberDTO=memberService.listOne(userDetails.getUsername());
        String memberName=memberDTO.getName();
        askCommentService.commentRegister(askCommentDTO, askId, memberName);
        redirectAttributes.addAttribute("askId", askId);
        return "redirect:/user/ask/detail";
    }

    @PostMapping("/modify")
    public String modify(Long askId, Long askCommentId, AskCommentDTO askCommentDTO, Authentication authentication, RedirectAttributes redirectAttributes) {
        askCommentService.commentModify(askCommentDTO, askCommentId, askId);

        redirectAttributes.addAttribute("askId",askId);
        return "redirect:/user/ask/detail";
    }

    @GetMapping("/delete")
    public String delete(Long askId, Long askCommentId, RedirectAttributes redirectAttributes) {
        askCommentService.commentDelete(askId, askCommentId);

        redirectAttributes.addAttribute("askId", askId);
        return "redirect:/user/ask/detail";

    }

}
