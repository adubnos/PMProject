package com.example.pmproject.Repository;

import com.example.pmproject.Entity.Basket;
import com.example.pmproject.Entity.Member;
import com.example.pmproject.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BasketRepository extends JpaRepository<Basket, Long> {

    Optional<Basket> findByMemberAndProduct(Member member, Product product);

    @Query("Select u From Basket u Where u.member.name = :memberName")
    Page<Basket> findByMemberNameList(@Param("memberName") String memberName, Pageable pageable);

    Page<Basket> findAll(Pageable pageable);

    @Modifying
    @Query("Update Basket u set u.quantity=:quantity where u.basketId=:basketId")
    void modifyQuantity(@Param("quantity") Integer quantity, @Param("basketId") Long basketId);


}
