package com.example.pmproject.DTO;

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

    private Long ask_id;

    private String member_name;

    @NotEmpty(message = "답변 내용은 필수 입력 사항입니다.")
    private String content;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
