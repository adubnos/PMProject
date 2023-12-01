package com.example.pmproject.Controller;

import com.example.pmproject.Constant.Role;
import com.example.pmproject.DTO.AskCommentDTO;
import com.example.pmproject.DTO.AskDTO;
import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.Service.AskCommentService;
import com.example.pmproject.Service.AskService;
import com.example.pmproject.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AskController {

    private final AskService askService;
    private final AskCommentService askCommentService;
    private final MemberService memberService;

    @GetMapping("/user/ask")
    public String askList(@PageableDefault(page=1) Pageable pageable, Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDTO memberDTO=memberService.listOne(userDetails.getUsername());
        String memberName = memberDTO.getName();

        Page<AskDTO> askDTOS=askService.askDTOS(memberName, pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), askDTOS.getTotalPages());
        if (endPage==0) {
            endPage=startPage;
        }

        model.addAttribute("askDTOS", askDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "user/ask";

    }

    @GetMapping( "/admin/ask/list")
    public String adminAskList(@PageableDefault(page=1) Pageable pageable, Authentication authentication, Model model) {

        Page<AskDTO> askDTOS = askService.askDTOS("", pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), askDTOS.getTotalPages());
        if (endPage==0) {
            endPage=startPage;
        }

        model.addAttribute("askDTOS", askDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/ask/list";

    }

    @GetMapping("/user/ask/detail")
    public String askDetail(Long askId, Model model) {
        AskDTO askDTO=askService.listOne(askId);
        List<AskCommentDTO> askCommentDTOList=askCommentService.askCommentDTOList(askId);

        model.addAttribute("askDTO", askDTO);
        model.addAttribute("askCommentDTO", askCommentDTOList);

        return "ask/detail";
    }

    @GetMapping("/user/ask/register")
    public String askRegisterForm(AskDTO askDTO) {
        return "ask/register";
    }

    @PostMapping("/user/ask/register")
    public String askRegister(@Valid AskDTO askDTO, BindingResult bindingResult, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String member_name = memberService.listOne(userDetails.getUsername()).getName();
        if(bindingResult.hasErrors()) {
            return "ask/register";
        }
        askService.register(askDTO, member_name);

        return "redirect:/user/ask";
    }

    @GetMapping("/user/ask/modify")
    public String askModifyForm(Long askId, Model model) {
        AskDTO askDTO=askService.listOne(askId);
        model.addAttribute("askDTO", askDTO);

        return "ask/modify";
    }

    @PostMapping("/user/ask/modify")
    public String askModify(@Valid AskDTO askDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "ask/modify";
        }
        askService.modify(askDTO);

        return "redirect:/user/ask";
    }

    @GetMapping("/user/ask/delete")
    public String askDelete(Long askId) {
        askService.delete(askId);
        return "redirect:/user/ask";
    }

}
