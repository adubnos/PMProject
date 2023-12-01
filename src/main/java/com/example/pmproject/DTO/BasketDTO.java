package com.example.pmproject.DTO;

import com.example.pmproject.Entity.Member;
import com.example.pmproject.Entity.Product;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BasketDTO {

    private Long basketId;

    private Product product;

    private Member member;

    private Integer quantity;

    private Integer tPrice;

    private String img;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

}
