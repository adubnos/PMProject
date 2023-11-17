package com.example.pmproject.Entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "shop")
@SequenceGenerator(sequenceName = "shop_SEQ", name = "shop_SEQ", allocationSize = 1)
public class Shop extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "shop_SEQ")
    private Long shopId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String location;

    @Column
    private String tel;

    @Column
    private String img;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    List<ShopComment> shopCommentList;
}
