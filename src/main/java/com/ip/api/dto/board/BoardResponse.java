package com.ip.api.dto.board;

import com.ip.api.domain.Board;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.BoardType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponse {
    private Long boardId;
    private String title;
    private String content;
    private String writerName;
    private String writerConnId;
    private BoardType type;
    private Integer viewCount;
    private Boolean isPinned;
    private LocalDateTime createdAt; // 작성 날짜 추가

    public static BoardResponse fromEntity(Board board) {
        User writer = board.getWriter();
        BoardResponse response = new BoardResponse();
        response.boardId = board.getBoardId();
        response.title = board.getTitle();
        response.content = board.getContent();
        response.writerName = writer != null ? writer.getUserName() : "익명";
        response.writerConnId = writer != null ? writer.getConnId() : "익명";
        response.type = board.getType();
        response.viewCount = board.getViewCount();
        response.isPinned = board.getIsPinned();
        response.createdAt = board.getCreatedAt(); // 작성 날짜 매핑
        return response;
    }
}
