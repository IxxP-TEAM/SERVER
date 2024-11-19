package com.ip.api.service;

import com.ip.api.domain.Board;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.BoardType;
import com.ip.api.dto.board.BoardRequest.BoardRequestDTO;
import com.ip.api.dto.board.BoardResponse;
import com.ip.api.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public BoardResponse createBoard(User user, BoardRequestDTO request) {
        try {
            String writerName = (request.getType() == BoardType.ANONYMOUS) ? "익명" : user.getUserName();

            Board board = Board.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .type(request.getType())
                    .isPinned(request.getIsPinned())
                    .viewCount(0)
                    .writer(request.getType() == BoardType.ANONYMOUS ? null : user) // 익명 게시판의 경우 null
                    .build();

            Board savedBoard = boardRepository.save(board);
            return BoardResponse.fromEntity(savedBoard);
        } catch (Exception e) {
            System.out.println("Error occurred while creating board: " + e.getMessage());
            throw e;
        }
    }



    @Transactional(readOnly = true)
    public Page<BoardResponse> getBoards(BoardType type, boolean pinnedOnly, Pageable pageable) {
        // 게시판 타입 및 고정 여부 필터링 처리
        if (type != null && pinnedOnly) {
            return boardRepository.findByTypeAndIsPinned(type, true, pageable)
                    .map(BoardResponse::fromEntity);
        } else if (type != null) {
            return boardRepository.findByType(type, pageable)
                    .map(BoardResponse::fromEntity);
        } else if (pinnedOnly) {
            return boardRepository.findByIsPinned(true, pageable)
                    .map(BoardResponse::fromEntity);
        } else {
            return boardRepository.findAll(pageable)
                    .map(BoardResponse::fromEntity);
        }
    }


    @Transactional
    public void incrementViewCount(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        board.incrementViewCount();
    }

    @Transactional(readOnly = true)
    public Page<BoardResponse> getHotPosts(Pageable pageable) {
        return boardRepository.findAllByOrderByViewCountDesc(pageable)
                .map(BoardResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<BoardResponse> getPinnedNotices() {
        return boardRepository.findByIsPinned(true)
                .stream()
                .map(BoardResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BoardResponse> getUnreadNotices(LocalDateTime lastCheckedAt) {
        return boardRepository.findByIsPinnedAndCreatedAtAfter(true, lastCheckedAt)
                .stream()
                .map(BoardResponse::fromEntity)
                .toList();
    }

    @Transactional
    public BoardResponse updateBoard(Long id, User user, BoardRequestDTO request) {
        // 게시글 찾기
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 작성자 확인
        if (board.getWriter() == null || !board.getWriter().getConnId().equals(user.getConnId())) {
            throw new IllegalStateException("작성자만 수정할 수 있습니다.");
        }

        // 게시글 업데이트
        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setIsPinned(request.getIsPinned());

        // 업데이트 후 반환
        return BoardResponse.fromEntity(board);
    }

    @Transactional
    public BoardResponse deleteBoard(Long id, User user) {
        // 게시글 찾기
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 작성자 확인
        if (board.getWriter() == null || !board.getWriter().getConnId().equals(user.getConnId())) {
            throw new IllegalStateException("작성자만 삭제할 수 있습니다.");
        }

        // 게시글 삭제 전 응답 데이터 생성
        BoardResponse response = BoardResponse.fromEntity(board);

        // 게시글 삭제
        boardRepository.delete(board);

        return response;
    }

}
