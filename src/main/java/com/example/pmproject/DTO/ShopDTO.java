package com.example.pmproject.DTO;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopDTO {

    private Long shopId;

    @NotEmpty(message = "매장 이름은 필수 입력 사항입니다.")
    private String name;

    @NotEmpty(message = "매장 설명은 필수 입력 사항입니다.")
    private String content;

    @NotEmpty(message = "매장 주소는 필수 입력 사항입니다.")
    private String location;

    @NotNull(message = "매장 전화 번호는 필수 입력 사항입니다.")
    private String tel;

    private String img;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
