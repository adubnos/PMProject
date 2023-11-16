package com.example.pmproject.DTO;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AskDTO {

    private Long askId;

    @NotEmpty(message = "문의 제목은 필수 입력 사항입니다.")
    private String title;

    private Integer type;

    @NotEmpty(message = "문의 내용은 필수 입력 사항입니다.")
    private String content;

    private String member_name;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}

