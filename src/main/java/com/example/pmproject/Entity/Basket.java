package com.example.pmproject.Entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "basket")
@SequenceGenerator(sequenceName = "basket_SEQ", name = "basket_SEQ", allocationSize = 1)
public class Basket extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "basket_SEQ")
    private Long basketId;

    @ManyToOne
    @JoinColumn(name="member_name", referencedColumnName = "name")
    private Member member;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;

    @Column
    private Integer quantity;

    @Column
    private Integer price;

}
