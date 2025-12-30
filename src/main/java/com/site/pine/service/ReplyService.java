package com.site.pine.service;

import com.site.pine.dto.member.MemberDto;
import com.site.pine.dto.reply.ReplyCreateReqDto;
import com.site.pine.dto.reply.ReplyResDto;
import com.site.pine.entity.Reply;
import com.site.pine.repository.MemberRepository;
import com.site.pine.repository.ReplyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class ReplyService {

    private final MemberRepository mr;
    private final ReplyRepository rr;


    public ReplyResDto createReply(MemberDto mdto, ReplyCreateReqDto dto) {

        if (mdto == null) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }

        Reply reply = new Reply();
        reply.setContent(dto.getContent());
        reply.setTargetId(dto.getTargetId());
        reply.setStatus(0);

        reply.setMember(
                mr.findById(mdto.getId())
                        .orElseThrow()
        );

        if (dto.getParentId() != null) {
            Reply parent = rr.findById(dto.getParentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글 없음"));
            reply.setParent(parent);
        }

        rr.save(reply);

        return ReplyResDto.from(reply);

    }

    public List<ReplyResDto> getReplyList(Long targetId) {
        List<Reply> parents =
                rr.findParentReplies(targetId);

        return parents.stream()
                .map(ReplyResDto::from)
                .toList();
    }
}
