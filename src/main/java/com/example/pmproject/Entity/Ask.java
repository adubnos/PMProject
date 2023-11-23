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
@Table
@SequenceGenerator(sequenceName = "ask_SEQ", name = "ask_SEQ", allocationSize = 1)
public class Ask extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ask_SEQ")
    private Long askId;

    @Column(nullable = false)
    private String title;

    @Column
    private Integer type;

    @Column(nullable = false)
    private String content;

    @Column
    private Boolean isAsk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_name", referencedColumnName = "name")
    private Member member;

    @OneToMany(mappedBy = "ask", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<AskComment> askCommentList;
}
