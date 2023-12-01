package com.example.pmproject.Service;

import com.example.pmproject.DTO.ProductDTO;
import com.example.pmproject.DTO.ProductDTO;
import com.example.pmproject.Entity.Product;
import com.example.pmproject.Entity.Product;
import com.example.pmproject.Repository.ProductRepository;
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
public class ProductService {
    
    @Value("${productImgUploadLocation}")
    private String productImgUploadLocation;
    private final S3Uploader s3Uploader;
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper=new ModelMapper();
    
    public Page<ProductDTO> productDTOS(Pageable pageable) {
        int page=pageable.getPageNumber()-1;
        int pageLimit=6;

        Page<Product> paging;

        paging=productRepository.findAll(PageRequest.of(page, pageLimit, Sort.Direction.DESC, "productId"));

        return paging.map(product -> ProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .img(product.getImg())
                .build());
    }

    public void register(ProductDTO productDTO, MultipartFile imgFile) throws IOException {
        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";

        if(originalFileName != null) {
            newFileName = s3Uploader.upload(imgFile, productImgUploadLocation);
        }
        productDTO.setImg(newFileName);
        Product product=modelMapper.map(productDTO, Product.class);
        productRepository.save(product);
    }

    public ProductDTO listOne(Long productId) {
        Product product=productRepository.findById(productId).orElseThrow();
        return modelMapper.map(product, ProductDTO.class);
    }

    public void modify(ProductDTO productDTO, MultipartFile imgFile) throws IOException {
        Product product = productRepository.findById(productDTO.getProductId()).orElseThrow();
        String deleteFile = product.getImg();

        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";

        if(originalFileName.length() != 0) {
            if(deleteFile.length() != 0) {
                s3Uploader.deleteFile(deleteFile, productImgUploadLocation);
            }

            newFileName = s3Uploader.upload(imgFile, productImgUploadLocation);
            productDTO.setImg(newFileName);
        }
        productDTO.setProductId(product.getProductId());
        Product modify=modelMapper.map(productDTO, Product.class);

        productRepository.save(modify);
    }

    public void delete(Long productId) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow();
        s3Uploader.deleteFile(product.getImg(), productImgUploadLocation);

        productRepository.deleteById(productId);
    }
}
