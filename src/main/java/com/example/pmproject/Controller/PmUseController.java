package com.example.pmproject.Controller;

import com.example.pmproject.Constant.Role;
import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.DTO.PmUseDTO;
import com.example.pmproject.Service.MemberService;
import com.example.pmproject.Service.PmUseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerMapping;

@Controller
@RequiredArgsConstructor
public class PmUseController {

    private final PmUseService pmUseService;
    private final MemberService memberService;

    @GetMapping({"/admin/pmUse/list","/member/pmUse/list"})
    public String pmUseList(@PageableDefault(page=1) Pageable pageable, Authentication authentication, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDTO memberDTO=memberService.listOne(userDetails.getUsername());
        String memberName= memberDTO.getName();
        Page<PmUseDTO> pmUseDTOS;

        if(memberDTO.getRole() == Role.ROLE_ADMIN) {
            pmUseDTOS=pmUseService.pmUseDTOS("",pageable);
        }else {
            pmUseDTOS=pmUseService.pmUseDTOS(memberName,pageable);
        }

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), pmUseDTOS.getTotalPages());

        model.addAttribute("pmUseDTOS", pmUseDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        if("/admin/pmUse/list".equals(RequestContextHolder.currentRequestAttributes().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST))) {
            return "admin/pmUse/list";
        }else {
            return "member/pmUse/list";
        }

    }
}
