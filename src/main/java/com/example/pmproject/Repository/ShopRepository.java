package com.example.pmproject.Repository;

import com.example.pmproject.Entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {

    Page<Shop> findByLocationContaining(String keyword, Pageable pageable);

}
