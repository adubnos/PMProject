package com.example.pmproject.Repository;

import com.example.pmproject.Entity.Pm;
import com.example.pmproject.Entity.PmUse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PmRepository extends JpaRepository<Pm, Long> {

    @Query("SELECT p FROM Pm p WHERE p.location Like %:keyword% And p.isUse = true")
    Page<Pm> findByLocation(@Param("keyword") String keyword, Pageable pageable);

}
