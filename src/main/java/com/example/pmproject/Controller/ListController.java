package com.example.pmproject.Controller;

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
