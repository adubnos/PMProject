package com.example.pmproject.Service;

import com.example.pmproject.Constant.Role;
import com.example.pmproject.DTO.ProductCommentDTO;
import com.example.pmproject.Entity.Member;
import com.example.pmproject.Entity.Product;
import com.example.pmproject.Entity.ProductComment;
import com.example.pmproject.Repository.MemberRepository;
import com.example.pmproject.Repository.ProductCommentRepository;
import com.example.pmproject.Repository.ProductRepository;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.method.P;
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
public class ProductCommentService {
    private final ProductCommentRepository productCommentRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper=new ModelMapper();

    public List<ProductCommentDTO> productCommentDTOS(Long product_id) {
        List<ProductComment> productComments=productCommentRepository.findByProductId(product_id);

        return productComments.stream()
                .map(comment -> modelMapper.map(comment, ProductCommentDTO.class))
                .collect(Collectors.toList());
    }

    public void commentRegister(ProductCommentDTO productCommentDTO, Long product_id, String member_name) {
        Product product=productRepository.findById(product_id).orElseThrow();
        Member member=memberRepository.findByName(member_name).orElseThrow();
        ProductComment productComment=ProductComment.builder()
                .product(product)
                .member(member)
                .content(productCommentDTO.getContent())
                .build();
        productCommentRepository.save(productComment);
    }

    public void commentModify(ProductCommentDTO productCommentDTO, Long productCommentId, Long productId) {
        Product product=productRepository.findById(productId).orElseThrow();
        ProductComment productComment=productCommentRepository.findById(productCommentId).orElseThrow();

        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String currentUserEmail=userDetails.getUsername();
        Member currentUser=memberRepository.findByEmail(currentUserEmail).orElseThrow();

        String commentAuthorEmail=productComment.getMember().getEmail();

        if (!currentUser.getEmail().equals(commentAuthorEmail)) {
            // 현재 사용자가 작성자가 아닌 경우, 예외를 throw 하거나 적절히 처리
            throw new AccessDeniedException("이 댓글을 수정할 권한이 없습니다");
        }

        ProductComment modify = modelMapper.map(productCommentDTO, ProductComment.class);
        modify.setProductCommentId(productComment.getProductCommentId());
        modify.setProduct(product);
        modify.setMember(productComment.getMember());
        productCommentRepository.save(modify);
    }

    public void commentDelete(String email, Long productCommentId) throws Exception {
        Optional<ProductComment> optionalProductComment = productCommentRepository.findById(productCommentId);

        if (optionalProductComment.isPresent()) {
            ProductComment productComment = optionalProductComment.get();

            // 현재 로그인한 사용자 가져오기
            Optional<Member> optionalMember = memberRepository.findByEmail(email);

            if (optionalMember.isPresent()) {
                Member currentUser = optionalMember.get();

                // 현재 사용자가 관리자 권한 또는 댓글 소유자인지 확인
                if (currentUser.getRole() == Role.ROLE_ADMIN || currentUser.getName().equals(productComment.getMember().getName())) {
                    // 사용자가 필요한 권한을 가지고 있을 때만 댓글을 삭제
                    productCommentRepository.deleteById(productCommentId);
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
