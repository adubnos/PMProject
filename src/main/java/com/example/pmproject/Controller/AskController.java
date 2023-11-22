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
    private final ModelMapper modelMapper=new ModelMapper();

    @GetMapping({"/admin/ask/list", "/member/ask/list"})
    public String askList(@PageableDefault(page=1) Pageable pageable, Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDTO memberDTO=memberService.listOne(userDetails.getUsername());
        String memberName = memberDTO.getName();
        Page<AskDTO> askDTOS;

        if(memberDTO.getRole() == Role.ROLE_ADMIN) {
            askDTOS=askService.askDTOS("", pageable);
        }else {
            askDTOS=askService.askDTOS(memberName, pageable);
        }

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), askDTOS.getTotalPages());

        model.addAttribute("askDTOS", askDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        if("/admin/ask/list".equals(RequestContextHolder.currentRequestAttributes().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST))) {
            return "admin/ask/list";
        }else {
            return "member/ask/list";
        }

    }

    @GetMapping({"/admin/ask/detail", "/member/ask/detail"})
    public String askDetail(Long askId, Model model) {
        AskDTO askDTO=askService.listOne(askId);
        List<AskCommentDTO> askCommentDTOList=askCommentService.askCommentDTOList(askId);

        model.addAttribute("askDTO", askDTO);
        model.addAttribute("askCommentDTO", askCommentDTOList);

        return "ask/detail";
    }

    @GetMapping("/member/ask/register")
    public String askRegisterForm(AskDTO askDTO) {
        return "ask/register";
    }

    @PostMapping("/member/ask/register")
    public String askRegister(@Valid AskDTO askDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "ask/register";
        }
        askService.register(askDTO);

        return "redirect:/";
    }

    @GetMapping("/member/ask/modify")
    public String askModifyForm(Long askId, Model model) {
        AskDTO askDTO=askService.listOne(askId);
        model.addAttribute("askDTO", askDTO);

        return "ask/modify";
    }

    @PostMapping("/member/ask/modify")
    public String askModify(@Valid AskDTO askDTO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return "ask/modify";
        }
        askService.modify(askDTO);

        return "redirect:/";
    }

    @GetMapping("/member/ask/delete")
    public String askDelete(Long askId) {
        askService.delete(askId);
        return "redirect:/";
    }

}
