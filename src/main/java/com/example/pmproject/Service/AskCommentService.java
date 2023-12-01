package com.example.pmproject.Service;

import com.example.pmproject.Constant.Role;
import com.example.pmproject.DTO.AskCommentDTO;
import com.example.pmproject.DTO.ProductCommentDTO;
import com.example.pmproject.Entity.*;
import com.example.pmproject.Repository.AskCommentRepository;
import com.example.pmproject.Repository.AskRepository;
import com.example.pmproject.Repository.MemberRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AskCommentService {

    private final AskCommentRepository askCommentRepository;
    private final AskRepository askRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper=new ModelMapper();

    public List<AskCommentDTO> askCommentDTOList(Long askId) {
        List<AskComment> askComments=askCommentRepository.findByAsk(askId);

        return askComments.stream()
                .map(comment -> modelMapper.map(comment, AskCommentDTO.class))
                .collect(Collectors.toList());

    }

    public void commentRegister(AskCommentDTO askCommentDTO, Long ask_id, String member_name) {
        Ask ask=askRepository.findById(ask_id).orElseThrow();
        Member member=memberRepository.findByName(member_name).orElseThrow();
        AskComment askComment=AskComment.builder()
                .ask(ask)
                .member(member)
                .content(askCommentDTO.getContent())
                .build();
        askRepository.cAsk(ask.getAskId());
        askCommentRepository.save(askComment);
    }

    public void commentModify(AskCommentDTO askCommentDTO, Long askCommentId, Long askId) {
        Ask ask=askRepository.findById(askId).orElseThrow();
        AskComment askComment=askCommentRepository.findById(askCommentId).orElseThrow();

        AskComment modify = modelMapper.map(askCommentDTO, AskComment.class);
        modify.setAskCommentId(askComment.getAskCommentId());
        modify.setAsk(ask);
        askCommentRepository.save(modify);
    }

    public void commentDelete(Long askId, Long askCommentId){
        askCommentRepository.deleteById(askCommentId);
        askRepository.nAsk(askId);
    }
}
