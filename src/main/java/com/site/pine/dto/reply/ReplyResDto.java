package com.site.pine.dto.reply;

import com.site.pine.entity.Reply;
import lombok.Builder;
import lombok.Getter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Builder
public class ReplyResDto {

    private Long id;
    private String content;
    private int status;
    private Timestamp writeDate;

    // 작성자 정보
    private Long memberId;
    private String nickname;
    private String profileImg;

    // 대댓글
    private List<ReplyResDto> children;

    public static ReplyResDto from(Reply reply) {

        return ReplyResDto.builder()
                .id(reply.getId())
                .content(reply.getContent())
                .status(reply.getStatus())
                .writeDate(reply.getWriteDate())

                .memberId(reply.getMember().getId())
                .nickname(reply.getMember().getNickname())
                .profileImg(reply.getMember().getProfile_img())

                .children(
                        reply.getChildren().stream()
                                .map(ReplyResDto::from)
                                .toList()
                )
                .build();
    }
}
