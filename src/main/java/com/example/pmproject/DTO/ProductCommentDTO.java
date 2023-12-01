package com.example.pmproject.DTO;

import com.example.pmproject.Entity.Member;
import com.example.pmproject.Entity.Product;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductCommentDTO {

    private Long productCommentId;

    private Product product;

    private Member member;

    @NotEmpty(message = "내용은 필수 입력 사항입니다.")
    private String content;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
