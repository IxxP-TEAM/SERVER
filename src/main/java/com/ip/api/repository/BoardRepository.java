package com.ip.api.repository;

import com.ip.api.domain.Board;
import com.ip.api.domain.enums.BoardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    Page<Board> findByType(BoardType type, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.type = :type AND b.isPinned = true ORDER BY b.createdAt DESC")
    List<Board> findPinnedNotices(@Param("type") BoardType type);

    @Query("SELECT b FROM Board b WHERE b.type = :type ORDER BY b.viewCount DESC")
    Page<Board> findHotPosts(@Param("type") BoardType type, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.type = :type AND b.createdAt > :lastCheckedAt")
    List<Board> findUnreadNotices(@Param("type") BoardType type, @Param("lastCheckedAt") LocalDateTime lastCheckedAt);

    // 고정된 게시글 조회
    Page<Board> findByIsPinnedTrue(Pageable pageable);
    Page<Board> findByTypeAndIsPinned(BoardType type, boolean isPinned, Pageable pageable); // 타입별 고정 게시글 조회
    Page<Board> findByIsPinned(boolean isPinned, Pageable pageable); // 고정된 게시글 조회
    List<Board> findByIsPinned(boolean isPinned); // 모든 고정 게시글
    List<Board> findByIsPinnedAndCreatedAtAfter(boolean isPinned, LocalDateTime createdAt); // 특정 시간 이후 고정 게시글
    Page<Board> findAllByOrderByViewCountDesc(Pageable pageable); // 조회수 기준 정렬
}

