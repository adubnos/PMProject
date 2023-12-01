package com.example.pmproject.Service;

import com.example.pmproject.Constant.Role;
import com.example.pmproject.DTO.ShopCommentDTO;
import com.example.pmproject.Entity.Member;
import com.example.pmproject.Entity.Shop;
import com.example.pmproject.Entity.ShopComment;
import com.example.pmproject.Repository.MemberRepository;
import com.example.pmproject.Repository.ShopCommentRepository;
import com.example.pmproject.Repository.ShopRepository;
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
public class ShopCommentService {
    private final ShopCommentRepository shopCommentRepository;
    private final ShopRepository shopRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper=new ModelMapper();

    public List<ShopCommentDTO> shopCommentDTOS(Long shop_id) {
        List<ShopComment> shopComments=shopCommentRepository.findByShopId(shop_id);

        return shopComments.stream()
                .map(comment -> modelMapper.map(comment, ShopCommentDTO.class))
                .collect(Collectors.toList());
    }

    public void commentRegister(ShopCommentDTO shopCommentDTO, Long shop_id, String member_name) {
        Shop shop=shopRepository.findById(shop_id).orElseThrow();
        Member member=memberRepository.findByName(member_name).orElseThrow();
        ShopComment shopComment=ShopComment.builder()
                .shop(shop)
                .member(member)
                .content(shopCommentDTO.getContent())
                .build();
        shopCommentRepository.save(shopComment);
    }

    public void commentModify(ShopCommentDTO shopCommentDTO, Long shopCommentId, Long shopId) {
        Shop shop=shopRepository.findById(shopId).orElseThrow();
        ShopComment shopComment=shopCommentRepository.findById(shopCommentId).orElseThrow();

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserEmail=userDetails.getUsername();
        Member currentUser=memberRepository.findByEmail(currentUserEmail).orElseThrow();

        String commentAuthorEmail=shopComment.getMember().getEmail();

        if (!currentUser.getEmail().equals(commentAuthorEmail)) {
            // 현재 사용자가 작성자가 아닌 경우, 예외를 throw하거나 적절히 처리
            throw new AccessDeniedException("이 댓글을 수정할 권한이 없습니다");
        }

        ShopComment modify = modelMapper.map(shopCommentDTO, ShopComment.class);
        modify.setShopCommentId(shopComment.getShopCommentId());
        modify.setMember(shopComment.getMember());
        modify.setShop(shop);
        shopCommentRepository.save(modify);
    }

    public void commentDelete(String email, Long shopCommentId) throws Exception {
        Optional<ShopComment> optionalShopComment = shopCommentRepository.findById(shopCommentId);

        if (optionalShopComment.isPresent()) {
            ShopComment shopComment = optionalShopComment.get();

            // 현재 로그인한 사용자 가져오기
            Optional<Member> optionalMember = memberRepository.findByEmail(email);

            if (optionalMember.isPresent()) {
                Member currentUser = optionalMember.get();

                // 현재 사용자가 관리자 권한 또는 댓글 소유자인지 확인
                if (currentUser.getRole() == Role.ROLE_ADMIN || currentUser.getName().equals(shopComment.getMember().getName())) {
                    // 사용자가 필요한 권한을 가지고 있을 때만 댓글을 삭제
                    shopCommentRepository.deleteById(shopCommentId);
                } else {
                    // 권한이 없는 삭제를 처리 (예외 던지기, 로깅 등)
                    throw new Exception("이 댓글을 삭제할 권한이 없습니다");
                }
            } else {
                // 사용자를 찾을 수 없음을 처리 (예외 던지기, 로깅 등)
                throw new Exception("사용자를 찾을 수 없습니다");
            }
        } else {
            // 댓글을 찾을 수 없음을 처리 (예외 던지기, 로깅 등)
            throw new Exception("댓글을 찾을 수 없습니다");
        }
    }
}
