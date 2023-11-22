package com.example.pmproject.Entity;

import com.example.pmproject.Constant.Role;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "member")
@SequenceGenerator(sequenceName = "member_SEQ", name = "member_SEQ", allocationSize = 1)
public class Member extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "member_SEQ")
    private Long memberId; //멤버 번호

    @Column(nullable = false, unique = true)
    private String email; //멤버 이메일(아이디)

    @Column(nullable = false)
    private String password; //멤버 비밀번호

    @Column(nullable = false, unique = true)
    private String name; //멤버 닉네임

    @Column
    private String address;

    @Column
    private String tel;

    @Column
    private Integer findPwdHint;

    @Column(nullable = false)
    private String findPwdAnswer; //비밀번호 찾기 질문

    @Column
    @Enumerated(EnumType.STRING)
    private Role role; //멤버 역할 (관리자 : ROLE_ADMIN / 유저 : ROLE_USER)

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<PmUse> useList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ProductComment> productCommentList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<ShopComment> shopCommentList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Ask> askList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<AskComment> askCommentList;
}
