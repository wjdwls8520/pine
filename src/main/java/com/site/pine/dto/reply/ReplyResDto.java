package com.site.pine.dto.reply;

import com.site.pine.entity.Reply;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Getter
@Builder
public class ReplyResDto {

    // ... 기존 필드 ...
    private Long id;
    private Long postId;
    private String content;
    private String deleteYN;
    private LocalDateTime writeDate;

    private Long memberId;
    private String nickname;
    private String profileImg;

    // 대댓글 개수
    private int childCount;
    // 대댓글 리스트
    private List<ReplyResDto> children;

    // 자식을 가져올지 말지 결정하는 플래그 추가
    public static ReplyResDto from(Reply reply, boolean includeChildren) {

        boolean isDeleted = "Y".equals(reply.getDeleteYN());

        ReplyResDtoBuilder builder = ReplyResDto.builder()
                .id(reply.getId())
                .content(isDeleted ? "삭제된 댓글입니다" : reply.getContent())
                .deleteYN(reply.getDeleteYN())
                .writeDate(reply.getWriteDate())
                .memberId(isDeleted ? null : reply.getMember().getId())
                .nickname(isDeleted ? "(알수없음)" : reply.getMember().getNickname())
                .profileImg(isDeleted ? null : reply.getMember().getProfile_img())

                .childCount(reply.getChildCount());

        // includeChildren이 true일 때만 자식을 변환
        if (includeChildren && reply.getChildren() != null) {
            builder.children(
                    reply.getChildren().stream()
                            .map(child -> ReplyResDto.from(child, false)) // 자식의 자식은 일단 조회 X
                            .toList()
            );
        } else {
            builder.children(new ArrayList<>()); // null 대신 빈 리스트
        }

        return builder.build();
    }

    // 기존 코드와의 호환성을 위해 오버로딩 (기본값: 자식 미포함)
    public static ReplyResDto from(Reply reply) {
        return from(reply, false);
    }
}