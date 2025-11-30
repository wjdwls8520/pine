package com.site.pine.dao;

import com.site.pine.entity.Member;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IMemberDao {

    Member findByEmail(String email);
}
