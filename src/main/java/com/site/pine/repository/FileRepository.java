package com.site.pine.repository;

import com.site.pine.dto.post.PostMainFileDto;
import com.site.pine.entity.File;
import com.site.pine.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Arrays;
import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    @Query("""

            select new com.site.pine.dto.post.PostMainFileDto(
        f.post.id,
        f.id,
        f.path,
        f.contentType,
        f.status
    )
    from File f
    where f.post.id in :postIds
    and f.status = 2
    """)
    List<PostMainFileDto> findFilesByPostIds(@Param("postIds") List<Long> postIds);

    List<File> findAllByPost(Post post);

    void deleteByPost(Post post);

    // 특정 게시글(postId)의 파일 중, 특정 타입(prefix, 예: "image/")으로 시작하는 파일 찾기
    @Query("select f from File f where f.post.id = :postId and f.contentType like concat(:prefix, '%')")
    List<File> findByPostIdAndContentTypeStartingWith(@Param("postId") Long postId, @Param("prefix") String prefix);

    // S3 파일 삭제를 위해 파일 목록 조회
    List<File> findAllByPost(Post post);

    // DB 데이터 일괄 삭제
    @Modifying
    @Query("DELETE FROM File f WHERE f.post = :post")
    void deleteAllByPost(@Param("post") Post post);
}