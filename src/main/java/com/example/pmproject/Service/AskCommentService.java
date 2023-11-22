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

        return Arrays.asList(modelMapper.map(askComments, AskCommentDTO[].class));
    }

    public void commentRegister(AskCommentDTO askCommentDTO, Long ask_id, String member_name) {
        Ask ask=askRepository.findById(ask_id).orElseThrow();
        Member member=memberRepository.findByName(member_name).orElseThrow();
        AskComment askComment=AskComment.builder()
                .ask(ask)
                .member(member)
                .content(askCommentDTO.getContent())
                .build();
        askCommentRepository.save(askComment);
    }

    public void commentModify(AskCommentDTO askCommentDTO, Long askCommentId, Long askId) {
        Ask ask=askRepository.findById(askId).orElseThrow();
        AskComment askComment=askCommentRepository.findById(askCommentId).orElseThrow();

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserEmail=userDetails.getUsername();
        Member currentUser=memberRepository.findByEmail(currentUserEmail).orElseThrow();

        String commentAuthorEmail=askComment.getMember().getEmail();

        if (!currentUser.getEmail().equals(commentAuthorEmail)) {
            // 현재 사용자가 작성자가 아닌 경우, 예외를 throw 하거나 적절히 처리
            throw new AccessDeniedException("이 글을 수정할 권한이 없습니다");
        }

        AskComment modify = modelMapper.map(askCommentDTO, AskComment.class);
        modify.setAskCommentId(askComment.getAskCommentId());
        modify.setAsk(ask);
        askCommentRepository.save(modify);
    }

    public void commentDelete(String email, Long askCommentId) throws Exception {
        Optional<AskComment> optionalAskComment = askCommentRepository.findById(askCommentId);

        if (optionalAskComment.isPresent()) {
            AskComment askComment = optionalAskComment.get();

            // 현재 로그인한 사용자 가져오기
            Optional<Member> optionalMember = memberRepository.findByEmail(email);

            if (optionalMember.isPresent()) {
                Member currentUser = optionalMember.get();

                // 현재 사용자가 관리자 권한 또는 댓글 소유자인지 확인
                if (currentUser.getRole() == Role.ROLE_ADMIN) {
                    // 사용자가 필요한 권한을 가지고 있을 때만 댓글을 삭제
                    askCommentRepository.deleteById(askCommentId);
                } else {
                    // 권한이 없는 삭제를 처리 (예외 던지기, 로깅 등)
                    throw new Exception("이 글을 삭제할 권한이 없습니다");
                }
            } else {
                // 사용자를 찾을 수 없음을 처리 (예외 던지기, 로깅 등)
                throw new UsernameNotFoundException("사용자를 찾을 수 없습니다");
            }
        } else {
            // 댓글을 찾을 수 없음을 처리 (예외 던지기, 로깅 등)
            throw new NotFoundException("글을 찾을 수 없습니다");
        }
    }
}
