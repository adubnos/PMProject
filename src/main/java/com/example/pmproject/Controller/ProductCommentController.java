package com.example.pmproject.Controller;

import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.DTO.ProductCommentDTO;
import com.example.pmproject.DTO.ProductCommentDTO;
import com.example.pmproject.DTO.ShopCommentDTO;
import com.example.pmproject.Service.MemberService;
import com.example.pmproject.Service.ProductCommentService;
import com.example.pmproject.Service.ShopCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product/detail")
public class ProductCommentController {

    private final ProductCommentService productCommentService;
    private final MemberService memberService;

    @PostMapping("/register")
    public String register(Long productId, ProductCommentDTO productCommentDTO, Authentication authentication, RedirectAttributes redirectAttributes) {
        UserDetails userDetails=(UserDetails) authentication.getPrincipal();
        MemberDTO memberDTO=memberService.listOne(userDetails.getUsername());
        String memberName=memberDTO.getName();
        productCommentService.commentRegister(productCommentDTO, productId, memberName);
        redirectAttributes.addAttribute("productId", productId);
        return "redirect:/user/product/detail";
    }

    @PostMapping("/modify")
    public String modify(Long productId, Long productCommentId, ProductCommentDTO productCommentDTO, Authentication authentication, RedirectAttributes redirectAttributes) {
        productCommentService.commentModify(productCommentDTO, productCommentId, productId);
        redirectAttributes.addAttribute("productId",productId);
        return "redirect:/user/product/detail";
    }

    @PostMapping("/delete")
    public String delete(Long productId, Long productCommentId, RedirectAttributes redirectAttributes, Authentication authentication) throws Exception {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDTO memberDTO = memberService.listOne(userDetails.getUsername());
        String email = memberDTO.getEmail();

        try {
            productCommentService.commentDelete(email, productCommentId);
        }catch (Exception e){
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/user/product/detail";
        }

        redirectAttributes.addAttribute("productId", productId);
        return "redirect:/user/product/detail";

    }
}
