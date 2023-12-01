package com.example.pmproject.Repository;

import com.example.pmproject.Entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    @Query("Select s From Shop s Where s.location Like %:keyword%")
    Page<Shop> findByLocation(@Param("keyword") String keyword, Pageable pageable);

}
