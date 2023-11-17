package com.example.pmproject.DTO;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PmDTO {

    private Long pmId;

    @NotEmpty(message = "PM 이름은 필수 입력 사항입니다.")
    private String name;

    private Integer type;

    private Boolean isUse;

    @NotEmpty(message = "현재 위치는 필수 입력 사항입니다.")
    private String location;

    private String img;
}
