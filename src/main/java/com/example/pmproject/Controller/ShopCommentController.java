package com.example.pmproject.Controller;

import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.DTO.ShopCommentDTO;
import com.example.pmproject.Service.MemberService;
import com.example.pmproject.Service.ShopCommentService;
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
@RequestMapping("/shop/comment")
public class ShopCommentController {

    private final ShopCommentService shopCommentService;
    private final MemberService memberService;

    @PostMapping("/register")
    public String register(Long shopId, ShopCommentDTO shopCommentDTO, Authentication authentication, RedirectAttributes redirectAttributes) {
        UserDetails userDetails=(UserDetails) authentication.getPrincipal();
        MemberDTO memberDTO=memberService.listOne(userDetails.getUsername());
        String memberName=memberDTO.getName();
        shopCommentService.commentRegister(shopCommentDTO, shopId, memberName);
        redirectAttributes.addAttribute("shopId", shopId);
        return "redirect:/user/shop/detail";
    }

    @PostMapping("/modify")
    public String modify(Long shopId, Long shopCommentId, ShopCommentDTO shopCommentDTO, Authentication authentication, RedirectAttributes redirectAttributes) {
        shopCommentService.commentModify(shopCommentDTO, shopCommentId, shopId);
        redirectAttributes.addAttribute("shopId",shopId);
        return "redirect:/user/shop/detail";
    }

    @GetMapping("/delete")
    public String delete(Long shopId, Long shopCommentId, RedirectAttributes redirectAttributes, Authentication authentication) throws Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDTO memberDTO = memberService.listOne(userDetails.getUsername());
        String email = memberDTO.getEmail();

        try {
            shopCommentService.commentDelete(email, shopCommentId);
        }catch (Exception e){
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/user/shop/detail";
        }

        redirectAttributes.addAttribute("shopId", shopId);
        return "redirect:/user/shop/detail";

    }
}
