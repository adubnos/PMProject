package com.example.pmproject.Controller;

import com.example.pmproject.Constant.Role;
import com.example.pmproject.DTO.BasketDTO;
import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.Service.BasketService;
import com.example.pmproject.Service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BasketController {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${productImgUploadLocation}")
    private String folder;
    private final BasketService basketService;
    private final MemberService memberService;

    @GetMapping({"/user/basket", "/admin/basket/list"})
    public String list(@PageableDefault(page = 1) Pageable pageable, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails)authentication.getPrincipal();
        MemberDTO memberDTO = memberService.listOne(userDetails.getUsername());
        String name = memberDTO.getName();
        Page<BasketDTO> basketDTOS;
        if ("/admin/basket".equals(RequestContextHolder.currentRequestAttributes().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST))) {
            basketDTOS = basketService.basketDTOS("", pageable);
        }else {
            basketDTOS = basketService.basketDTOS(name, pageable);
        }

        for (BasketDTO basketDTO : basketDTOS) {
            int quantity = basketDTO.getQuantity();
            int price = basketDTO.getProduct().getPrice();
            Integer tPrice = quantity * price;
            basketDTO.setTPrice(tPrice);
        }


        int blockLimit = 10;

        int startPage = (((int) (Math.ceil((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        int endPage = Math.min((startPage + blockLimit - 1), basketDTOS.getTotalPages());
        if (endPage==0) {
            endPage=startPage;
        }

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("basketDTOS", basketDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        if ("/admin/basket/list".equals(RequestContextHolder.currentRequestAttributes().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST))) {
            return "admin/basket/list";
        } else {
            return "user/basket";
        }
    }


    @PostMapping("/user/basket/add")
    public String add(Integer quantity, Long productId, Authentication authentication, RedirectAttributes redirectAttributes) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        MemberDTO memberDTO=memberService.listOne(userDetails.getUsername());
        Role role = memberDTO.getRole();
        String name = memberDTO.getName();
        try{
            basketService.addToCart(name, quantity, productId);
        }catch(IllegalStateException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
        }

        redirectAttributes.addAttribute("productId", productId);

        return "redirect:/user/product/detail";
    }

    @PostMapping("/user/basket/modify")
    public String modify(Integer quantity, Long basketId, RedirectAttributes redirectAttributes) {
        Integer tPrice = basketService.modifyBasket(quantity, basketId);

        redirectAttributes.addAttribute("tPrice", tPrice);
        return "redirect:/user/basket";
    }

    @GetMapping("/user/basket/delete")
    public String delete(Long basketId) {
        basketService.delete(basketId);

        return "redirect:/user/basket";
    }

}
