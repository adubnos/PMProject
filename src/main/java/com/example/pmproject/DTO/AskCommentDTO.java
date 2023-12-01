package com.example.pmproject.DTO;

import com.example.pmproject.Entity.Ask;
import com.example.pmproject.Entity.Member;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AskCommentDTO {

    private Long askCommentId;

    private Ask ask;

    private Member member;

    @NotEmpty(message = "답변 내용은 필수 입력 사항입니다.")
    private String content;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
