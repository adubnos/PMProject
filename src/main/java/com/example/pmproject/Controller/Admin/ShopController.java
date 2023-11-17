package com.example.pmproject.Controller.Admin;

import com.example.pmproject.DTO.ShopDTO;
import com.example.pmproject.Service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/shop")
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/list/shop")
    public String shopList(@PageableDefault(page=1) Pageable pageable, Model model) {
        Page<ShopDTO> shopDTOS=shopService.shopDTOS(null, pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), shopDTOS.getTotalPages());

        model.addAttribute("shopDTOS", shopDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/shop/list";
    }
}
