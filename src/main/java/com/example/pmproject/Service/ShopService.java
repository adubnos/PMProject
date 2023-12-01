package com.example.pmproject.Service;

import com.example.pmproject.DTO.ShopDTO;
import com.example.pmproject.Entity.Shop;
import com.example.pmproject.Repository.ShopRepository;
import com.example.pmproject.Util.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    @Value("${shopImgUploadLocation}")
    private String shopImgUploadLocation;
    private final S3Uploader s3Uploader;
    private final ShopRepository shopRepository;
    private final ModelMapper modelMapper=new ModelMapper();

    public Page<ShopDTO> shopDTOS(String keyword, Pageable pageable) {
        int page=pageable.getPageNumber()-1;
        int pageLimit=5;

        Page<Shop> paging;
        if(!Objects.equals(keyword, "")) {
            paging=shopRepository.findByLocation(keyword, PageRequest.of(page, pageLimit, Sort.Direction.ASC, "shopId"));
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

    public void register(ShopDTO shopDTO, MultipartFile imgFile) throws IOException {
        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";

        if(originalFileName != null) {
            newFileName = s3Uploader.upload(imgFile, shopImgUploadLocation);
        }
        shopDTO.setImg(newFileName);
        Shop shop = modelMapper.map(shopDTO, Shop.class);
        shopRepository.save(shop);
    }

    public ShopDTO listOne(Long shopId) {
        Shop shop = shopRepository.findById(shopId).orElseThrow();
        return modelMapper.map(shop, ShopDTO.class);
    }

    public void modify(ShopDTO shopDTO, MultipartFile imgFile) throws IOException {
        Shop shop = shopRepository.findById(shopDTO.getShopId()).orElseThrow();
        String deleteFile = shop.getImg();

        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";

        if(originalFileName.length() != 0) {
            if(deleteFile.length() != 0 ) {
                s3Uploader.deleteFile(deleteFile, shopImgUploadLocation);
            }

            newFileName = s3Uploader.upload(imgFile, shopImgUploadLocation);
            shopDTO.setImg(newFileName);
        }
        shopDTO.setShopId(shop.getShopId());
        Shop modify = modelMapper.map(shopDTO, Shop.class);

        shopRepository.save(modify);
    }

    public void delete(Long shopId) throws IOException {
        Shop shop = shopRepository.findById(shopId).orElseThrow();
        s3Uploader.deleteFile(shop.getImg(), shopImgUploadLocation);

        shopRepository.deleteById(shopId);
    }
}
