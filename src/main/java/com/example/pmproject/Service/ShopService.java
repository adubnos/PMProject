package com.example.pmproject.Service;

import com.example.pmproject.DTO.ShopDTO;
import com.example.pmproject.Entity.Shop;
import com.example.pmproject.Repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    @Value("${shopImgUploadLocation}")
    private String shopImgUploadLocation;
    private final ShopRepository shopRepository;
    private final ModelMapper modelMapper=new ModelMapper();

    public Page<ShopDTO> shopDTOS(String keyword, Pageable pageable) {
        int page=pageable.getPageNumber()-1;
        int pageLimit=5;

        Page<Shop> paging;
        if(keyword != null) {
            paging=shopRepository.findByLocationContaining(keyword, PageRequest.of(page, pageLimit, Sort.Direction.ASC, "shopId"));
        }else {
            paging=shopRepository.findAll(PageRequest.of(page, pageLimit, Sort.Direction.ASC, "shopId"));
        }

        return paging.map(shop -> ShopDTO.builder()
                .shopId(shop.getShopId())
                .name(shop.getName())
                .content(shop.getContent())
                .location(shop.getLocation())
                .tel(shop.getTel())
                .img(shop.getImg())
                .build());
    }
}
