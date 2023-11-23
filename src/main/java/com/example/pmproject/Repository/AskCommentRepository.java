package com.example.pmproject.Repository;

import com.example.pmproject.Entity.AskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AskCommentRepository extends JpaRepository<AskComment, Long> {

    @Query("select u From AskComment u Where u.ask.askId = :askId")
    List<AskComment> findByAsk(@Param("askId") Long askId);

    @Query("select u From AskComment u Where u.member.name = :memberName")
    List<AskComment> findByMemberName(@Param("memberName") String memberName);

}
