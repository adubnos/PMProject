package com.example.pmproject.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasketDTO {

    private Long basketId;

    private Long product_id;

    private String member_name;

    private String product_name;

    private Integer quantity;

    private Integer price;

    private Integer tPrice;

    private String img;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
