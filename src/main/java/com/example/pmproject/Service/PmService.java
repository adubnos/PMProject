package com.example.pmproject.Service;

import com.example.pmproject.DTO.PmDTO;
import com.example.pmproject.Entity.Pm;
import com.example.pmproject.Entity.PmUse;
import com.example.pmproject.Repository.PmRepository;
import com.example.pmproject.Repository.PmUseRepository;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PmService {

    @Value("${pmImgUploadLocation}")
    private String pmImgUploadLocation;
    private final imgService imgService;
    private final PmRepository pmRepository;
    private final ModelMapper modelMapper=new ModelMapper();

    public Page<PmDTO> pmDTOS(String keyword, Pageable pageable) {
        int page= pageable.getPageNumber()-1;
        int pageLimit=5;

        Page<Pm> paging;

        if(keyword != null) {
            paging=pmRepository.searchByLocation(keyword, PageRequest.of(page, pageLimit, Sort.Direction.ASC, "pmId"));
        }else {
            paging=pmRepository.findAll(PageRequest.of(page, pageLimit, Sort.Direction.ASC, "pmId"));
        }

        return paging.map(pm -> PmDTO.builder()
                .pmId(pm.getPmId())
                .type(pm.getType())
                .location(pm.getLocation())
                .img(pm.getImg())
                .build());

    }

    public void register(PmDTO pmDTO, MultipartFile imgFile) throws IOException {
        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";

        if(originalFileName != null) {
            newFileName = imgService.uploadFile(pmImgUploadLocation, originalFileName, imgFile.getBytes());
        }
        pmDTO.setImg(newFileName);
        Pm pm=modelMapper.map(pmDTO, Pm.class);
        pmRepository.save(pm);
    }

    public PmDTO listOne(Long pmId) {
        Pm pm=pmRepository.findById(pmId).orElseThrow();
        return modelMapper.map(pm, PmDTO.class);
    }

    public void modify(PmDTO pmDTO, MultipartFile imgFile) throws IOException {
        Pm pm = pmRepository.findById(pmDTO.getPmId()).orElseThrow();
        String deleteFile = pm.getImg();

        String originalFileName = imgFile.getOriginalFilename();
        String newFileName = "";

        if(originalFileName.length() != 0) {
            if(deleteFile.length() != 0) {
                imgService.deleteFile(pmImgUploadLocation, deleteFile);
            }

            newFileName = imgService.uploadFile(pmImgUploadLocation, originalFileName, imgFile.getBytes());
            pmDTO.setImg(newFileName);
        }
        pmDTO.setPmId(pm.getPmId());
        Pm modify=modelMapper.map(pmDTO, Pm.class);

        pmRepository.save(modify);
    }

    public void delete(Long pmId) {
        Pm pm = pmRepository.findById(pmId).orElseThrow();
        imgService.deleteFile(pmImgUploadLocation, pm.getImg());

        pmRepository.deleteById(pmId);
    }

}
