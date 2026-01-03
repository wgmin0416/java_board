package com.example.board.controller;

import com.example.board.dto.BoardRequest;
import com.example.board.dto.BoardResponse;
import com.example.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * BoardController: 게시판 웹 요청 처리 계층
 * 
 * Controller의 역할:
 * 1. HTTP 요청을 받아서 처리
 * 2. 요청 데이터를 Service에 전달
 * 3. Service의 결과를 받아서 화면에 전달
 * 
 * @Controller: 이 클래스가 Controller 계층임을 표시
 *              Spring MVC가 이 클래스를 인식하고 웹 요청을 처리
 *              HTML 뷰를 반환 (Thymeleaf 템플릿 사용)
 * 
 * @RequestMapping("/boards"): 이 컨트롤러의 모든 메서드의 기본 URL 경로
 *                             예: /boards, /boards/1, /boards/new 등
 * 
 * @RequiredArgsConstructor: final 필드의 생성자 자동 생성
 *                          BoardService를 의존성 주입받기 위해 사용
 */
@Controller
@RequestMapping("/boards")  // 모든 URL이 /boards로 시작
@RequiredArgsConstructor
public class BoardController {
    
    /**
     * BoardService: 비즈니스 로직 처리 계층
     * Spring이 자동으로 주입해줌 (의존성 주입)
     */
    private final BoardService boardService;
    
    /**
     * 게시글 목록 조회
     * 
     * @GetMapping: HTTP GET 요청을 처리
     *              URL: GET /boards
     * 
     * @PageableDefault(size = 10): 페이징 기본값 설정
     *                              - size = 10: 한 페이지에 10개씩 표시
     *                              - page = 0: 첫 페이지 (기본값)
     * 
     * @param pageable 페이징 정보 (URL에서 ?page=0&size=10 형태로 전달)
     * @param model 뷰에 데이터를 전달하기 위한 객체
     *              - model.addAttribute("key", value)로 데이터 추가
     *              - Thymeleaf 템플릿에서 ${key}로 사용 가능
     * @return 뷰 이름 (templates/board/list.html)
     */
    @GetMapping
    public String getAllBoards(
            @PageableDefault(size = 10) Pageable pageable,
            Model model) {
        // Service에서 페이징된 게시글 목록 가져오기
        Page<BoardResponse> boards = boardService.getAllBoards(pageable);
        
        // Model에 데이터 추가 (뷰로 전달)
        model.addAttribute("boards", boards);           // 게시글 목록
        model.addAttribute("currentPage", boards.getNumber());  // 현재 페이지 번호
        model.addAttribute("totalPages", boards.getTotalPages()); // 전체 페이지 수
        
        // "board/list" → templates/board/list.html 파일을 찾아서 렌더링
        return "board/list";
    }
    
    /**
     * 게시글 상세 조회
     * 
     * @GetMapping("/{id}"): 경로 변수 사용
     *                       URL: GET /boards/1 (1이 id에 들어감)
     * 
     * @PathVariable: URL 경로에서 변수 추출
     *                예: /boards/1 → id = 1
     * 
     * @param id 게시글 번호
     * @param model 뷰에 데이터 전달
     * @return 뷰 이름 (templates/board/detail.html)
     */
    @GetMapping("/{id}")
    public String getBoard(@PathVariable Long id, Model model) {
        // Service에서 게시글 조회
        BoardResponse board = boardService.getBoard(id);
        
        // Model에 게시글 정보 추가
        model.addAttribute("board", board);
        
        // "board/detail" → templates/board/detail.html
        return "board/detail";
    }
    
    /**
     * 게시글 작성 폼 페이지
     * 
     * @GetMapping("/new"): URL: GET /boards/new
     * 
     * @param model 뷰에 데이터 전달
     * @return 뷰 이름 (templates/board/form.html)
     */
    @GetMapping("/new")
    public String createBoardForm(Model model) {
        // 빈 BoardRequest 객체를 만들어서 폼에 전달
        // Thymeleaf에서 th:object="${board}"로 사용
        model.addAttribute("board", new BoardRequest());
        
        // "board/form" → templates/board/form.html
        return "board/form";
    }
    
    /**
     * 게시글 작성 처리
     * 
     * @PostMapping: HTTP POST 요청을 처리
     *               URL: POST /boards
     *               폼에서 submit 버튼을 누르면 이 메서드가 실행됨
     * 
     * @ModelAttribute: 폼에서 전송된 데이터를 자동으로 BoardRequest 객체로 변환
     *                  예: <input name="title"> → request.setTitle(...)
     * 
     * @param request 폼에서 전송된 게시글 데이터
     * @return 리다이렉트 (작성 후 목록 페이지로 이동)
     */
    @PostMapping
    public String createBoard(@ModelAttribute BoardRequest request) {
        // Service에서 게시글 저장
        boardService.createBoard(request);
        
        // "redirect:/boards": /boards로 리다이렉트
        // 작성 완료 후 목록 페이지로 자동 이동
        return "redirect:/boards";
    }
    
    /**
     * 게시글 수정 폼 페이지
     * 
     * @GetMapping("/{id}/edit"): URL: GET /boards/1/edit
     * 
     * @param id 수정할 게시글 번호
     * @param model 뷰에 데이터 전달
     * @return 뷰 이름 (templates/board/form.html)
     */
    @GetMapping("/{id}/edit")
    public String updateBoardForm(@PathVariable Long id, Model model) {
        // 기존 게시글 정보 조회
        BoardResponse board = boardService.getBoard(id);
        
        // BoardResponse를 BoardRequest로 변환
        BoardRequest request = new BoardRequest();
        request.setTitle(board.getTitle());
        request.setContent(board.getContent());
        request.setAuthor(board.getAuthor());
        
        // Model에 데이터 추가
        model.addAttribute("board", request);
        model.addAttribute("id", id);  // 수정 폼에서 사용할 게시글 번호
        
        // "board/form" → templates/board/form.html (작성 폼과 동일)
        return "board/form";
    }
    
    /**
     * 게시글 수정 처리
     * 
     * @PostMapping("/{id}"): URL: POST /boards/1
     *                        수정 폼에서 submit하면 이 메서드 실행
     * 
     * @param id 수정할 게시글 번호
     * @param request 수정할 내용
     * @return 리다이렉트 (수정 후 상세 페이지로 이동)
     */
    @PostMapping("/{id}")
    public String updateBoard(@PathVariable Long id, @ModelAttribute BoardRequest request) {
        // Service에서 게시글 수정
        boardService.updateBoard(id, request);
        
        // "redirect:/boards/{id}": 수정 후 상세 페이지로 이동
        return "redirect:/boards/" + id;
    }
    
    /**
     * 게시글 삭제 처리
     * 
     * @PostMapping("/{id}/delete"): URL: POST /boards/1/delete
     *                               삭제 버튼을 누르면 이 메서드 실행
     * 
     * @param id 삭제할 게시글 번호
     * @return 리다이렉트 (삭제 후 목록 페이지로 이동)
     */
    @PostMapping("/{id}/delete")
    public String deleteBoard(@PathVariable Long id) {
        // Service에서 게시글 삭제
        boardService.deleteBoard(id);
        
        // "redirect:/boards": 삭제 후 목록 페이지로 이동
        return "redirect:/boards";
    }
}

