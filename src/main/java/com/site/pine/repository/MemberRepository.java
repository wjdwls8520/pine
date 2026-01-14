package com.site.pine.repository;

import com.site.pine.dto.search.SearchMemberDto;
import com.site.pine.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Member findByEmail(String email);

    Member findByNickname(String nickname);

    // [추가] 랜덤으로 멤버 조회 (MySQL 기준 function('RAND') 사용)
    @Query("SELECT m FROM Member m ORDER BY function('RAND')")
    Page<Member> findRandomMembers(Pageable pageable);


    //멤버 검색
    @Query("SELECT new com.site.pine.dto.search.SearchMemberDto(" +
            "  m.id, m.nickname, m.profile_img, m.profile_msg " +
            ") " +
            "FROM Member m " +
            "WHERE m.nickname LIKE %:keyword%")
    List<SearchMemberDto> searchMembers(@Param("keyword") String keyword, Pageable pageable);
}
