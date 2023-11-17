package com.example.pmproject.Controller.Admin;

import com.example.pmproject.DTO.*;
import com.example.pmproject.Service.*;
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
@RequestMapping("/admin")
public class ListController {

    private final PmUseService pmUseService;
    private final ProductService productService;
    private final AskService askService;


    @GetMapping("/list/pmUse")
    public String pmUseList(@PageableDefault(page=1) Pageable pageable, Model model) {
        Page<PmUseDTO> pmUseDTOS=pmUseService.pmUseDTOS(null,pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), pmUseDTOS.getTotalPages());

        model.addAttribute("pmUseDTOS", pmUseDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/pmUse/list";
    }

    @GetMapping("/list/product")
    public String productList(@PageableDefault(page=1) Pageable pageable, Model model) {
        Page<ProductDTO> productDTOS=productService.productDTOS(null, pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), productDTOS.getTotalPages());

        model.addAttribute("productDTOS", productDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/product/list";
    }



    @GetMapping("/list/ask")
    public String askList(@PageableDefault(page=1) Pageable pageable, Model model) {
        Page<AskDTO> askDTOS=askService.askDTOS(null, pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), askDTOS.getTotalPages());

        model.addAttribute("askDTOS", askDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/ask/list";
    }


}
