package com.site.pine.dto.reply;

import com.site.pine.entity.Reply;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReplyResDto {

    private Long id;
    private Long postId;
    private String content;
    private String deleteYN;
    private LocalDateTime writeDate;

    // 작성자 정보
    private Long memberId;
    private String nickname;
    private String profileImg;

    // 대댓글
    private List<ReplyResDto> children;

    public static ReplyResDto from(Reply reply) {

        boolean isDeleted = "Y".equals(reply.getDeleteYN());

        return ReplyResDto.builder()
            .id(reply.getId())
            .content(isDeleted ? "삭제된 댓글입니다" : reply.getContent())
            .deleteYN(reply.getDeleteYN())
            .writeDate(reply.getWriteDate())

            .memberId(isDeleted ? null : reply.getMember().getId())
            .nickname(isDeleted ? "(알수없음)" : reply.getMember().getNickname())
            .profileImg(isDeleted ? null : reply.getMember().getProfile_img())

            .children(
                    reply.getChildren().stream()
                            .map(ReplyResDto::from)
                            .toList()
            )
            .build();
    }
}
