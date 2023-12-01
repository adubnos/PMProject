package com.example.pmproject.Controller;

import com.example.pmproject.DTO.PmDTO;
import com.example.pmproject.DTO.PmUseDTO;
import com.example.pmproject.Service.PmService;
import com.example.pmproject.Service.PmUseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class PmController {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${pmImgUploadLocation}")
    private String folder;
    private final PmService pmService;
    private final PmUseService pmUseService;

    @GetMapping({"/admin/pm/list","/user/pm/list"})
    public String pmList(@PageableDefault(page=1) Pageable pageable, @RequestParam(value = "keyword", defaultValue = "") String keyword, Model model) {
        Page<PmDTO> pmDTOS=pmService.pmDTOS(keyword, pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), pmDTOS.getTotalPages());
        if (endPage==0) {
            endPage=startPage;
        }

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("pmDTOS", pmDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("keyword", keyword);

        if("/admin/pm/list".equals(RequestContextHolder.currentRequestAttributes().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST))) {
            return "admin/pm/list";
        }else {
            return "pm/list";
        }
    }

    @GetMapping("/admin/pm/detail")
    public String pmDetail(Long pmId, Model model) {
        PmDTO pmDTO=pmService.listOne(pmId);
        List<PmUseDTO> pmUseDTOList=pmUseService.pmUseList(pmId);

        model.addAttribute("pmDTO", pmDTO);
        model.addAttribute("pmUseDTO", pmUseDTOList);
        return "admin/pm/detail";
    }

    @GetMapping("/admin/pm/register")
    public String pmRegisterForm(PmDTO pmDTO) {
        return "admin/pm/register";
    }

    @PostMapping("/admin/pm/register")
    public String pmRegister(@Valid PmDTO pmDTO, MultipartFile imgFile, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()) {
            return "admin/pm/register";
        }
        pmService.register(pmDTO, imgFile);

        return "redirect:/admin/pm/list";
    }

    @GetMapping("/admin/pm/modify")
    public String pmModifyForm(Long pmId, Model model) {
        PmDTO pmDTO=pmService.listOne(pmId);
        model.addAttribute("pmDTO", pmDTO);

        return "admin/pm/modify";
    }

    @PostMapping("/admin/pm/modify")
    public String pmModify(@Valid PmDTO pmDTO, MultipartFile imgFile, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()) {
            return "admin/pm/modify";
        }
        pmService.modify(pmDTO, imgFile);

        return "redirect:/admin/pm/list";
    }

    @GetMapping("/admin/pm/delete")
    public String pmDelete(Long pmId) throws IOException {
        pmService.delete(pmId);
        return "redirect:/admin/pm/list";
    }
}
