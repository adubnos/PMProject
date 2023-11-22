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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AskService {

    private final AskRepository askRepository;
    private final ModelMapper modelMapper=new ModelMapper();

    public Page<AskDTO> askDTOS(@RequestParam(value = "memberName", defaultValue = "") String memberName, Pageable pageable) {
        int page=pageable.getPageNumber()-1;
        int pageLimit=5;

        Page<Ask> paging;
        if(!Objects.equals(memberName, "")) {
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

    public void register(AskDTO askDTO) {
        Ask ask=modelMapper.map(askDTO, Ask.class);
        askRepository.save(ask);
    }

    public AskDTO listOne(Long askId) {
        Optional<Ask> ask=askRepository.findById(askId);
        return modelMapper.map(ask, AskDTO.class);
    }

    public void modify(AskDTO askDTO) {
        Long askId=askDTO.getAskId();
        Ask ask=askRepository.findById(askId).orElseThrow();

        Ask modify=modelMapper.map(askDTO, Ask.class);
        modify.setAskId(ask.getAskId());
        askRepository.save(modify);
    }

    public void delete(Long askId) {
        askRepository.deleteById(askId);
    }
}
