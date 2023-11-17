package com.example.pmproject.Controller.Admin;

import com.example.pmproject.DTO.PmDTO;
import com.example.pmproject.Service.PmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/pm")
public class PmController {

    private final PmService pmService;

    @GetMapping("/list")
    public String pmList(@PageableDefault(page=1) Pageable pageable, Model model) {
        Page<PmDTO> pmDTOS=pmService.pmDTOS(null,pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), pmDTOS.getTotalPages());

        model.addAttribute("pmDTOS", pmDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/pm/list";
    }

    @GetMapping("/register")
    public String pmRegisterForm(PmDTO pmDTO) {
        return "admin/pm/register";
    }

    @PostMapping("/register")
    public String pmRegister(@Valid PmDTO pmDTO, MultipartFile imgFile, BindingResult bindingResult, Model model) throws IOException {
        if(bindingResult.hasErrors()) {
            return "admin/pm/register";
        }
        pmService.register(pmDTO, imgFile);

        return "redirect:/admin/pm/list";
    }

    @GetMapping("/modify")
    public String pmModifyForm(Long pmId, Model model) {
        PmDTO pmDTO=pmService.listOne(pmId);
        model.addAttribute("pmDTO", pmDTO);

        return "admin/pm/modify";
    }
}
