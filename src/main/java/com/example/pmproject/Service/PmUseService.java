package com.example.pmproject.Service;

import com.example.pmproject.DTO.PmDTO;
import com.example.pmproject.DTO.PmUseDTO;
import com.example.pmproject.Entity.Member;
import com.example.pmproject.Entity.Pm;
import com.example.pmproject.Entity.PmUse;
import com.example.pmproject.Repository.MemberRepository;
import com.example.pmproject.Repository.PmRepository;
import com.example.pmproject.Repository.PmUseRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PmUseService {

    private final PmUseRepository pmUseRepository;
    private final PmRepository pmRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper=new ModelMapper();

    public Page<PmUseDTO> pmUseDTOS(String memberName, Pageable pageable) {
        int page = pageable.getPageNumber()-1;
        int pageLimit=5;

        Page<PmUse> paging;

        if(!Objects.equals(memberName, "")) {
            paging=pmUseRepository.findByMemberNameList(memberName, PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "pmUseId")));
        }else {
            paging=pmUseRepository.findAll(PageRequest.of(page,pageLimit, Sort.by(Sort.Direction.DESC,"pmUseId")));
        }

        return paging.map(pmUse -> PmUseDTO.builder()
                .pmUseId(pmUse.getPmUseId())
                .isUse(pmUse.getIsUse())
                .startLocation(pmUse.getStartLocation())
                .finishLocation(pmUse.getFinishLocation())
                .regDate(pmUse.getRegDate())
                .modDate(pmUse.getModDate())
                .build());
    }

    public List<PmUseDTO> pmUseList(Long pmId) {
        List<PmUse> pmUses=pmUseRepository.findByPm(pmId);

        return Arrays.asList(modelMapper.map(pmUses, PmUseDTO[].class));
    }

    public void register(Long pmId, String name) {
        Pm pm=pmRepository.findById(pmId).orElseThrow();
        Member member = memberRepository.findByName(name).orElseThrow();
        PmUse pmUse = PmUse.builder()
                .startLocation(pm.getLocation())
                .pm(pm)
                .member(member)
                .build();
        pmUseRepository.save(pmUse);
    }

    public void modify(PmUseDTO pmUseDTO) {
        PmUse pmUse = pmUseRepository.findById(pmUseDTO.getPmUseId()).orElseThrow();
        Optional<Pm> pmOptional = pmRepository.findById(pmUseDTO.getPm_id());

        if (pmOptional.isPresent()) {
            Pm pm = pmOptional.get();

            // PmUse 업데이트
            pmUse.setFinishLocation(pmUseDTO.getLocation());
            pmUse.setIsUse(true);

            // Pm 업데이트
            pm.setIsUse(true);
            pm.setLocation(pmUseDTO.getLocation());

            pmUseRepository.save(pmUse);
            pmRepository.save(pm);
        } else {
            // Pm을 찾지 못한 경우 처리
            throw new EntityNotFoundException("ID에 해당하는 Pm을 찾을 수 없습니다: " + pmUseDTO.getPm_id());
        }
    }

    public void updatePmLocation() {
        List<Pm> pmList = pmRepository.findAll();

        for (Pm pm : pmList) {
            PmUse lastPmUse = pmUseRepository.findTopByPmOrderByPmUseIdDesc(pm);

            if (lastPmUse != null) {
                String locationToUpdate = lastPmUse.getFinishLocation() != null ?
                        lastPmUse.getFinishLocation() :
                        lastPmUse.getStartLocation();

                pm.setLocation(locationToUpdate);
                pmRepository.save(pm);
            }
        }
    }
}
