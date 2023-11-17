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
@Table(name = "pm")
@SequenceGenerator(sequenceName = "pm_SEQ", name = "pm_SEQ", allocationSize = 1)
public class Pm {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pm_SEQ")
    private Long pmId;

    @Column(nullable = false)
    private String name;

    @Column
    private Integer type;

    @Column
    private Boolean isUse;

    @Column(nullable = false)
    private String location;

    @Column
    private String img;

    @OneToMany(mappedBy = "pm", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<PmUse> pmUseList;


}
