package com.example.pmproject.Controller;

import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.DTO.MemberUpdateDTO;
import com.example.pmproject.Service.GlobalService;
import com.example.pmproject.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/list")
    public String list(@PageableDefault(page=1) Pageable pageable, Model model) {
        Page<MemberDTO> memberDTOS=memberService.memberDTOS(pageable);

        int blockLimit=5;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), memberDTOS.getTotalPages());

        model.addAttribute("memberDTOS", memberDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "member/list";
    }

    @GetMapping("/info")
    public String memberInfo(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDTO member = memberService.listOne(userDetails.getUsername());

        model.addAttribute("member", member);

        return "member/info/info";
    }

    @GetMapping("/update")
    public String updateForm(MemberUpdateDTO memberUpdateDTO, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDTO memberDTO=memberService.listOne(userDetails.getUsername());
        model.addAttribute("memberDTO", memberDTO);

        return "member/update";
    }

    @PostMapping("/update")
    public String update(@Valid MemberUpdateDTO memberUpdateDTO, MemberDTO memberDTO, BindingResult bindingResult, Model model, Authentication authentication) {
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

    @GetMapping("/withdrawal")
    public String withdrawalForm() {
        return "member/withdrawal";
    }

    @PostMapping("/withdrawal")
    public String withdrawal(@RequestParam String password, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        boolean result = memberService.withdrawal(userDetails.getUsername(), password);

        if(result) {
            return "redirect:/logout";
        }else {
            model.addAttribute("wrongPwd", "비밀번호가 맞지 않습니다.");
            return "member/withdrawal";
        }
    }
}
