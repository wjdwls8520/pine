package com.site.pine.service;

import com.site.pine.dao.IMemberDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    IMemberDao mdao;
}
