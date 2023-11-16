package com.example.pmproject.Repository;

import com.example.pmproject.Entity.Ask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AskRepository extends JpaRepository<Ask, Long> {

    @Query("select u from Ask u where u.member.name = :memberName")
    Page<Ask> findByMemberNameList(@Param("memberName") String memberName, Pageable pageable);
}
