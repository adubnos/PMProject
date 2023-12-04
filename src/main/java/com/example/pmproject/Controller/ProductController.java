package com.example.pmproject.Controller;

import com.example.pmproject.Constant.Role;
import com.example.pmproject.DTO.ProductCommentDTO;
import com.example.pmproject.DTO.ProductDTO;
import com.example.pmproject.Entity.ProductComment;
import com.example.pmproject.Service.MemberService;
import com.example.pmproject.Service.ProductCommentService;
import com.example.pmproject.Service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${productImgUploadLocation}")
    private String folder;
    private final ProductService productService;
    private final ProductCommentService productCommentService;
    private final MemberService memberService;

    @GetMapping({"/admin/product/list", "/user/product/list"})
    public String productList(@PageableDefault(page = 1) Pageable pageable, Model model) {
        Page<ProductDTO> productDTOS = productService.productDTOS(pageable);

        int blockLimit = 10;

        int startPage = (((int) (Math.ceil((double) pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        int endPage = Math.min((startPage + blockLimit - 1), productDTOS.getTotalPages());
        if (endPage==0) {
            endPage=startPage;
        }

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("productDTOS", productDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);

        if ("/admin/product/list".equals(RequestContextHolder.currentRequestAttributes().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST))) {
            return "admin/product/list";
        } else {
            return "product/list";
        }
    }

    @GetMapping("/user/product/detail")
    public String detail(Long productId, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String name = memberService.listOne(userDetails.getUsername()).getName();
        Role role = memberService.listOne(userDetails.getUsername()).getRole();
        ProductDTO productDTO=productService.listOne(productId);
        List<ProductCommentDTO> productCommentList=productCommentService.productCommentDTOS(productId);

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("productDTO", productDTO);
        model.addAttribute("name", name);
        model.addAttribute("role", role);
        model.addAttribute("productComment", productCommentList);
        return "product/detail";
    }


    @GetMapping("/admin/product/register")
    public String productRegisterForm(ProductDTO productDTO) {
        return "admin/product/register";
    }

    @PostMapping("/admin/product/register")
    public String productRegister(@Valid ProductDTO productDTO, MultipartFile imgFile, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()) {
            return "admin/product/register";
        }
        productService.register(productDTO, imgFile);

        return "redirect:/admin/product/list";
    }

    @GetMapping("/admin/product/modify")
    public String productModifyForm(Long productId, Model model) {
        ProductDTO productDTO=productService.listOne(productId);
        model.addAttribute("productDTO", productDTO);

        return "admin/product/modify";
    }

    @PostMapping("/admin/product/modify")
    public String productModify(@Valid ProductDTO productDTO, MultipartFile imgFile, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()) {
            return "admin/product/modify";
        }
        productService.modify(productDTO, imgFile);

        return "redirect:/admin/product/list";
    }

    @GetMapping("/admin/product/delete")
    public String productDelete(Long productId) throws IOException {
        productService.delete(productId);
        return "redirect:/admin/product/list";
    }
}
