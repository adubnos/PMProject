package com.example.pmproject.Repository;

import com.example.pmproject.Entity.Ask;
import com.example.pmproject.Entity.AskComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AskRepository extends JpaRepository<Ask, Long> {

    @Query("select u from Ask u where u.member.name = :memberName")
    Page<Ask> findByMemberNameList(@Param("memberName") String memberName, Pageable pageable);

}
