package com.example.pmproject.Repository;

import com.example.pmproject.Entity.Member;
import com.example.pmproject.Entity.Pm;
import com.example.pmproject.Entity.PmUse;
import com.example.pmproject.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PmUseRepository extends JpaRepository<PmUse, Long> {

    @Query("select u From PmUse u Where u.pm.pmId = :pmId")
    List<PmUse> findByPm(@Param("pmId") Long pmId);

    @Query("select u from PmUse u where u.member.name = :memberName")
    List<PmUse> findByMemberName(String memberName);

    @Query("select u from PmUse u where u.member.name = :memberName")
    Page<PmUse> findByMemberNameList(String memberName, Pageable pageable);

}
