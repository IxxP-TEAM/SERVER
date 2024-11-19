package com.ip.api.controller;

import com.ip.api.apiPayload.code.ApiResponse;
import com.ip.api.auth.AuthUser;
import com.ip.api.domain.User;
import com.ip.api.domain.enums.BoardType;
import com.ip.api.dto.board.BoardRequest;
import com.ip.api.dto.board.BoardRequest.BoardRequestDTO;
import com.ip.api.dto.board.BoardResponse;
import com.ip.api.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardController {

    @Autowired
    private final BoardService boardService;

    @PostMapping("/create")
    public ApiResponse<BoardResponse> createBoard(@AuthUser User user, @RequestBody BoardRequestDTO request) {
        System.out.println("User: " + user.getConnId()); // 로그인된 유저 확인
        System.out.println("Request: " + request);       // 요청 데이터 확인
        BoardResponse response = boardService.createBoard(user, request);
        return ApiResponse.of(response);
    }


    // 게시글 목록 조회 (필터링 + 페이징)
    @GetMapping("/list")
    public ApiResponse<Page<BoardResponse>> getBoards(
            @RequestParam(required = false) BoardType type, // 게시판 타입 필터
            @RequestParam(defaultValue = "false") boolean pinnedOnly, // 고정된 게시글만 조회 여부
            Pageable pageable)
    {
        Page<BoardResponse> responses = boardService.getBoards(type, pinnedOnly, pageable);
        return ApiResponse.of(responses);
    }


    @GetMapping("/notices/pinned")
    public ApiResponse<List<BoardResponse>> getPinnedNotices() {
        List<BoardResponse> response = boardService.getPinnedNotices();
        return ApiResponse.of(response);
    }

    @GetMapping("/hot")
    public ApiResponse<Page<BoardResponse>> getHotPosts(Pageable pageable) {
        Page<BoardResponse> response = boardService.getHotPosts(pageable);
        return ApiResponse.of(response);
    }

    @GetMapping("/notices/unread")
    public ApiResponse<List<BoardResponse>> getUnreadNotices(@RequestParam LocalDateTime lastCheckedAt) {
        List<BoardResponse> response = boardService.getUnreadNotices(lastCheckedAt);
        return ApiResponse.of(response);
    }

    @PatchMapping("/{id}")
    public ApiResponse<BoardResponse> updateBoard(
            @PathVariable Long id,
            @AuthUser User user,
            @RequestBody BoardRequest.BoardRequestDTO request) {
        BoardResponse response = boardService.updateBoard(id, user, request);
        return ApiResponse.of(response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<BoardResponse> deleteBoard(@PathVariable Long id, @AuthUser User user) {
        BoardResponse response =boardService.deleteBoard(id, user);
        return ApiResponse.of(response);
    }

}
