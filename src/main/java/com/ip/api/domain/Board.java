package com.ip.api.domain;

import com.ip.api.domain.common.BaseEntity;
import com.ip.api.domain.enums.BoardType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId; // 게시글 ID

    private String title; // 제목

    @Column(columnDefinition = "LONGTEXT")
    private String content; // 내용

    @Enumerated(EnumType.STRING)
    private BoardType type; // 게시판 타입 (공지사항, 자유게시판, 익명게시판)

    private Boolean isPinned; // 공지사항 고정 여부

    private Integer viewCount; // 조회수

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User writer; // 작성자 (익명 게시판은 nullable 가능)

    public void incrementViewCount() {
        this.viewCount = this.viewCount == null ? 1 : this.viewCount + 1;
    }
}
