package com.example.pmproject.Controller;

import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/member")
public class AdminMemberController {

    private final MemberService memberService;

    @GetMapping("/list")
    public String memberList(@PageableDefault(page=1) Pageable pageable, Model model) {
        Page<MemberDTO> memberDTOS=memberService.memberDTOS(pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), memberDTOS.getTotalPages());
        if (endPage==0) {
            endPage=startPage;
        }

        model.addAttribute("memberDTOS", memberDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/member/list";
    }

    @GetMapping("/withdrawal")
    public String memberWithdrawal(@RequestParam("email") String email) {
        memberService.adminWithdrawal(email);
        return "redirect:/admin/member/list";
    }
}
