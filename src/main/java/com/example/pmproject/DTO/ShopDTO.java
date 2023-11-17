package com.example.pmproject.DTO;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopDTO {

    private Long shopId;

    @NotNull(message = "매장 이름은 필수 입력 사항입니다.")
    private String name;

    @NotNull(message = "매장 설명은 필수 입력 사항입니다.")
    private String content;

    @NotNull(message = "매장 주소는 필수 입력 사항입니다.")
    private String location;

    private String tel;

    private String img;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
