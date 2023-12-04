package com.example.pmproject.Controller;

import com.example.pmproject.Constant.Role;
import com.example.pmproject.DTO.MemberDTO;
import com.example.pmproject.DTO.ShopCommentDTO;
import com.example.pmproject.DTO.ShopDTO;
import com.example.pmproject.Service.MemberService;
import com.example.pmproject.Service.ShopCommentService;
import com.example.pmproject.Service.ShopService;
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
public class ShopController {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;
    @Value("${shopImgUploadLocation}")
    private String folder;
    private final ShopService shopService;
    private final ShopCommentService shopCommentService;
    private final MemberService memberService;

    @GetMapping({"/admin/shop/list","/user/shop/list"})
    public String shopList(@PageableDefault(page=1) Pageable pageable, @RequestParam(value = "keyword", defaultValue = "") String keyword, Model model) {
        Page<ShopDTO> shopDTOS=shopService.shopDTOS(keyword, pageable);

        int blockLimit=10;

        int startPage=(((int)(Math.ceil((double)pageable.getPageNumber()/blockLimit)))-1)*blockLimit+1;
        int endPage=Math.min((startPage+blockLimit-1), shopDTOS.getTotalPages());
        if (endPage==0) {
            endPage=startPage;
        }

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("shopDTOS", shopDTOS);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        model.addAttribute("keyword", keyword);

        if("/admin/shop/list".equals(RequestContextHolder.currentRequestAttributes().getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST))) {
            return "admin/shop/list";
        }else {
            return "shop/list";
        }
    }

    @GetMapping("/user/shop/detail")
    public String shopDetail(Long shopId, Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String name = memberService.listOne(userDetails.getUsername()).getName();
        Role role = memberService.listOne(userDetails.getUsername()).getRole();
        ShopDTO shopDTO=shopService.listOne(shopId);
        List<ShopCommentDTO> shopCommentDTOList=shopCommentService.shopCommentDTOS(shopId);

        model.addAttribute("bucket", bucket);
        model.addAttribute("region", region);
        model.addAttribute("folder", folder);
        model.addAttribute("shopDTO", shopDTO);
        model.addAttribute("name", name);
        model.addAttribute("role", role);
        model.addAttribute("shopComment", shopCommentDTOList);

        return "shop/detail";
    }

    @GetMapping("/admin/shop/register")
    public String shopRegisterForm(ShopDTO shopDTO) {
        return "admin/shop/register";
    }

    @PostMapping("/admin/shop/register")
    public String shopRegister(@Valid ShopDTO shopDTO, MultipartFile imgFile, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()) {
            return "admin/shop/register";
        }
        shopService.register(shopDTO, imgFile);

        return "redirect:/admin/shop/list";
    }

    @GetMapping("/admin/shop/modify")
    public String shopModifyForm(Long shopId, Model model) {
        ShopDTO shopDTO=shopService.listOne(shopId);
        model.addAttribute("shopDTO", shopDTO);

        return "admin/shop/modify";
    }

    @PostMapping("/admin/shop/modify")
    public String shopModify(@Valid ShopDTO shopDTO, MultipartFile imgFile, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()) {
            return "admin/shop/modify";
        }
        shopService.modify(shopDTO, imgFile);

        return "redirect:/admin/shop/list";
    }

    @GetMapping("/admin/shop/delete")
    public String shopDelete(Long shopId) throws IOException {
        shopService.delete(shopId);
        return "redirect:/admin/shop/list";
    }
}
