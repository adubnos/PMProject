package com.example.pmproject.Controller.Admin;

import com.example.pmproject.DTO.ShopDTO;
import com.example.pmproject.Service.ShopService;
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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/shop")
public class ShopController {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${shopImgUploadLocation}")
    private String folder;
    private final ShopService shopService;

    @GetMapping("/list/shop")
    public String shopList(@PageableDefault(page=1) Pageable pageable, Model model) {
        Page<ShopDTO> shopDTOS=shopService.shopDTOS(null, pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), shopDTOS.getTotalPages());

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("shopDTOS", shopDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/shop/list";
    }

    @GetMapping("/register")
    public String shopRegisterForm(ShopDTO shopDTO) {
        return "admin/shop/register";
    }

    @PostMapping("/register")
    public String shopRegister(@Valid ShopDTO shopDTO, MultipartFile imgFile, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()) {
            return "admin/shop/register";
        }
        shopService.register(shopDTO, imgFile);

        return "redirect:/admin/shop/list";
    }

    @GetMapping("/modify")
    public String shopModifyForm(Long shopId, Model model) {
        ShopDTO shopDTO=shopService.listOne(shopId);
        model.addAttribute("shopDTO", shopDTO);

        return "admin/shop/modify";
    }

    @PostMapping("/modify")
    public String shopModify(@Valid ShopDTO shopDTO, MultipartFile imgFile, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()) {
            return "admin/shop/modify";
        }
        shopService.modify(shopDTO, imgFile);

        return "redirect:/admin/shop/list";
    }

    @GetMapping("/delete")
    public String shopDelete(Long shopId) throws IOException {
        shopService.delete(shopId);
        return "redirect:/admin/shop/list";
    }
}
