package com.example.pmproject.Service;

import com.example.pmproject.DTO.AskDTO;
import com.example.pmproject.Entity.Ask;
import com.example.pmproject.Repository.AskRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AskService {

    private final AskRepository askRepository;
    private final ModelMapper modelMapper=new ModelMapper();

    public Page<AskDTO> askDTOS(String memberName, Pageable pageable) {
        int page=pageable.getPageNumber()-1;
        int pageLimit=5;

        Page<Ask> paging;
        if(memberName != null) {
            paging=askRepository.findByMemberNameList(memberName, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "askId")));
        }else {
            paging=askRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "askId")));
        }


        return paging.map(ask -> AskDTO.builder()
                .askId(ask.getAskId())
                .title(ask.getTitle())
                .regDate(ask.getRegDate())
                .modDate(ask.getModDate())
                .build());
    }
}
