package com.example.pmproject.Controller.Admin;

import com.example.pmproject.DTO.ProductDTO;
import com.example.pmproject.Service.ProductService;
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
@RequestMapping("/admin/product")
public class ProductController {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${productImgUploadLocation}")
    private String folder;
    private final ProductService productService;

    @GetMapping("/list")
    public String productList(@PageableDefault(page=1) Pageable pageable, Model model) {
        Page<ProductDTO> productDTOS=productService.productDTOS(null, pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), productDTOS.getTotalPages());

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("productDTOS", productDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        return "admin/product/list";
    }

    @GetMapping("/register")
    public String productRegisterForm(ProductDTO productDTO) {
        return "admin/product/register";
    }

    @PostMapping("/register")
    public String productRegister(@Valid ProductDTO productDTO, MultipartFile imgFile, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()) {
            return "admin/product/register";
        }
        productService.register(productDTO, imgFile);

        return "redirect:/admin/product/list";
    }

    @GetMapping("/modify")
    public String productModifyForm(Long productId, Model model) {
        ProductDTO productDTO=productService.listOne(productId);
        model.addAttribute("productDTO", productDTO);

        return "admin/product/modify";
    }

    @PostMapping("/modify")
    public String productModify(@Valid ProductDTO productDTO, MultipartFile imgFile, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()) {
            return "admin/product/modify";
        }
        productService.modify(productDTO, imgFile);

        return "redirect:/admin/product/list";
    }

    @GetMapping("/delete")
    public String productDelete(Long productId) throws IOException {
        productService.delete(productId);
        return "redirect:/admin/product/list";
    }
}
