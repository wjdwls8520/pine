package com.site.pine.repository;

import com.site.pine.dto.community.PostMainFileDto;
import com.site.pine.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

}
