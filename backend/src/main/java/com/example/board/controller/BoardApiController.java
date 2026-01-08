package com.example.board.controller;

import com.example.board.dto.BoardRequest;
import com.example.board.dto.BoardResponse;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * BoardApiController: 게시판 REST API 컨트롤러
 * 
 * React 등 클라이언트 애플리케이션에서 사용할 수 있는 JSON API를 제공합니다.
 * 
 * @RestController: 이 클래스가 REST API 컨트롤러임을 표시
 *                  - @Controller + @ResponseBody의 조합
 *                  - 모든 메서드의 반환값이 HTTP 응답 본문에 직접 작성됨
 *                  - JSON 형식으로 데이터 반환
 * 
 * @RequestMapping("/api/boards"): 이 컨트롤러의 모든 메서드의 기본 URL 경로
 *                                 예: /api/boards, /api/boards/1 등
 *                                 기존 BoardController(/boards)와 구분하기 위해 /api 추가
 * 
 * @RequiredArgsConstructor: final 필드의 생성자 자동 생성
 *                          BoardService를 의존성 주입받기 위해 사용
 */
@RestController                    // REST API 컨트롤러 (JSON 반환)
@RequestMapping("/api/boards")     // API URL 경로: /api/boards
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")  // Vite 기본 포트 (CORS 허용)
public class BoardApiController {
    
    /**
     * BoardService: 비즈니스 로직 처리 계층
     * Spring이 자동으로 주입해줌 (의존성 주입)
     */
    private final BoardService boardService;
    
    /**
     * 게시글 목록 조회 (페이징)
     * 
     * @GetMapping: HTTP GET 요청 처리
     *              - URL: GET /api/boards
     *              - 쿼리 파라미터: ?page=0&size=10&keyword=아이패드&searchType=title
     * 
     * @param pageable 페이징 정보
     *                 - page: 페이지 번호 (0부터 시작)
     *                 - size: 페이지 크기 (기본값: 10)
     * 
     * @param keyword 검색 키워드 (선택적)
     * 
     * @param searchType 검색 타입 (선택적, 기본값: title+content)
     * 
     * @return ResponseEntity<Page<BoardResponse>>
     *         - HTTP 상태 코드와 함께 페이징된 게시글 목록 반환
     *         - JSON 형식: { content: [...], totalElements: 100, totalPages: 10, ... }
     */
    @GetMapping
    public ResponseEntity<Page<BoardResponse>> getAllBoards(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "title+content") String searchType) {
        
        Page<BoardResponse> boards;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Elasticsearch로 검색
            boards = boardService.searchBoards(keyword.trim(), searchType, pageable);
        } else {
            // 전체 목록 조회
            boards = boardService.getAllBoards(pageable);
        }
        
        return ResponseEntity.ok(boards);
    }
    
    /**
     * 게시글 상세 조회
     * 
     * @GetMapping("/{id}"): 경로 변수 사용
     *                       - URL: GET /api/boards/1
     * 
     * @param id 게시글 번호
     * 
     * @return ResponseEntity<BoardResponse>
     *         - 게시글 정보 반환
     *         - 게시글 없으면 404 NOT_FOUND
     */
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> getBoard(@PathVariable Long id) {
        BoardResponse board = boardService.getBoard(id);
        return ResponseEntity.ok(board);
    }
    
    /**
     * 게시글 작성
     * 
     * @PostMapping: HTTP POST 요청 처리
     *               - URL: POST /api/boards
     *               - 요청 본문: JSON 형식의 BoardRequest
     * 
     * @param request 게시글 작성 요청 데이터
     *                - @RequestBody: HTTP 요청 본문(JSON)을 객체로 변환
     * 
     * @return ResponseEntity<BoardResponse>
     *         - 작성된 게시글 정보 반환
     *         - HTTP 상태 코드: 201 CREATED
     */
    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@RequestBody BoardRequest request) {
        BoardResponse board = boardService.createBoard(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(board);
    }
    
    /**
     * 게시글 수정
     * 
     * @PutMapping("/{id}"): HTTP PUT 요청 처리
     *                       - URL: PUT /api/boards/1
     *                       - 요청 본문: JSON 형식의 BoardRequest
     * 
     * @param id 수정할 게시글 번호
     * @param request 수정할 내용
     * 
     * @return ResponseEntity<BoardResponse>
     *         - 수정된 게시글 정보 반환
     */
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> updateBoard(
            @PathVariable Long id,
            @RequestBody BoardRequest request) {
        BoardResponse board = boardService.updateBoard(id, request);
        return ResponseEntity.ok(board);
    }
    
    /**
     * 게시글 삭제
     * 
     * @DeleteMapping("/{id}"): HTTP DELETE 요청 처리
     *                          - URL: DELETE /api/boards/1
     * 
     * @param id 삭제할 게시글 번호
     * 
     * @return ResponseEntity<Void>
     *         - 성공 시 HTTP 상태 코드: 204 NO_CONTENT
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
        return ResponseEntity.noContent().build();
    }
}
