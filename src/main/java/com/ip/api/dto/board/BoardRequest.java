package com.ip.api.dto.board;

import com.ip.api.domain.enums.BoardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BoardRequest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardRequestDTO {
        private String title;       // 게시글 제목
        private String content;     // 게시글 내용
        private BoardType type;     // 게시글 타입 (공지사항, 자유게시판 등)
        private Boolean isPinned;   // 고정 여부
    }

}
