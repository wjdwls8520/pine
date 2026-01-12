package com.site.pine.repository;

import com.site.pine.dto.post.PostMainFileDto;
import com.site.pine.entity.File;
import com.site.pine.entity.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
