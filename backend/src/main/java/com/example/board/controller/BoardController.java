package com.example.board.controller;

// DTO (Data Transfer Object): 계층 간 데이터 전달용 객체
import com.example.board.dto.BoardRequest;      // 요청 데이터 DTO (사용자 입력 → Service)
import com.example.board.dto.BoardResponse;     // 응답 데이터 DTO (Service → 사용자)
// Service 계층: 비즈니스 로직 처리
import com.example.board.service.BoardService;
// Lombok: 코드 자동 생성 라이브러리
import lombok.RequiredArgsConstructor;           // final 필드의 생성자 자동 생성
// Spring Data: 페이징 관련 클래스
import org.springframework.data.domain.Page;      // 페이징된 결과를 담는 객체 (데이터 + 전체 개수, 전체 페이지 수)
import org.springframework.data.domain.Pageable;  // 페이징 정보를 담는 객체 (페이지 번호, 크기, 정렬)
import org.springframework.data.web.PageableDefault; // 페이징 기본값 설정 어노테이션
// Spring MVC: 웹 요청 처리 관련
import org.springframework.stereotype.Controller;  // 컨트롤러 표시 어노테이션 (HTML 뷰 반환)
import org.springframework.ui.Model;             // 뷰에 데이터를 전달하기 위한 객체
import org.springframework.web.bind.annotation.*; // 웹 요청 매핑 어노테이션들 (@GetMapping, @PostMapping 등)

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
     * 
     * 의존성 주입 (Dependency Injection):
     * - private: 외부에서 직접 접근 불가
     * - final: 한 번 할당되면 변경 불가 (생성자에서만 초기화 가능)
     * - BoardService: 비즈니스 로직을 처리하는 서비스 객체
     * 
     * @RequiredArgsConstructor가 이 필드를 위한 생성자를 자동 생성:
     *   public BoardController(BoardService boardService) {
     *       this.boardService = boardService;
     *   }
     * 
     * Spring이 애플리케이션 시작 시 이 생성자를 호출하면서
     * BoardService 객체를 자동으로 주입해줌
     * → 우리는 new BoardService() 같은 코드를 작성할 필요 없음!
     */
    private final BoardService boardService;
    
    /**
     * 게시글 목록 조회
     * 
     * @GetMapping: HTTP GET 요청을 처리하는 어노테이션
     *              - URL: GET /boards
     *              - 브라우저에서 /boards 접속 시 이 메서드 실행
     *              - 다른 HTTP 메서드: @PostMapping, @PutMapping, @DeleteMapping
     * 
     * @PageableDefault(size = 10): 페이징 기본값 설정 어노테이션
     *                              - size = 10: 한 페이지에 10개씩 표시 (기본값)
     *                              - page = 0: 첫 페이지 (기본값, 생략 가능)
     *                              - URL에서 ?page=2&size=20 형태로 전달 가능
     *                              - 예: /boards?page=0&size=10
     * 
     * @param pageable 페이징 정보를 담는 객체
     *                 - Pageable: 페이지 번호, 크기, 정렬 정보를 담음
     *                 - URL에서 ?page=0&size=10 형태로 전달
     *                 - Spring이 자동으로 Pageable 객체 생성해서 전달
     *                 - pageable.getPageNumber(): 현재 페이지 번호 (0부터 시작)
     *                 - pageable.getPageSize(): 한 페이지에 보여줄 개수
     * 
     * @param keyword 검색 키워드 (선택적 파라미터)
     *                 - @RequestParam: URL 쿼리 파라미터에서 값 가져오기
     *                 - required = false: 없어도 됨 (필수 아님)
     *                 - 예: /boards?keyword=아이패드 → keyword = "아이패드"
     *                 - keyword.trim(): 앞뒤 공백 제거
     * 
     * @param searchType 검색 타입 (선택적 파라미터)
     *                   - defaultValue = "title+content": 기본값 설정
     *                   - 가능한 값: "title", "content", "title+content", "author"
     *                   - 예: /boards?keyword=아이패드&searchType=title
     * 
     * @param model 뷰에 데이터를 전달하기 위한 객체
     *              - Spring이 자동으로 생성해서 전달
     *              - model.addAttribute("key", value): 뷰에 데이터 추가
     *              - Thymeleaf 템플릿에서 ${key}로 사용 가능
     *              - 예: model.addAttribute("boards", boards) → 뷰에서 ${boards}
     * 
     * @return 뷰 이름 (String)
     *         - "board/list": templates/board/list.html 파일을 찾아서 렌더링
     *         - application.yml의 thymeleaf.prefix 설정에 따라
     *           classpath:/templates/board/list.html 경로에서 찾음
     *         - 뷰 렌더링: URL은 변경되지 않고 같은 URL에서 HTML만 렌더링
     */
    @GetMapping
    public String getAllBoards(
            @PageableDefault(size = 10) Pageable pageable,  // 페이징 정보 (기본값: size=10, page=0)
            @RequestParam(required = false) String keyword,  // 검색 키워드 (선택적, URL: ?keyword=아이패드)
            @RequestParam(required = false, defaultValue = "title+content") String searchType,  // 검색 타입 (기본값: title+content)
            Model model) {  // 뷰에 데이터 전달용 객체 (Spring이 자동 생성)
        
        // 페이징된 게시글 목록을 담을 변수
        // Page<BoardResponse>: 페이징된 결과 (데이터 리스트 + 전체 개수, 전체 페이지 수 등)
        Page<BoardResponse> boards;
        
        // 검색 키워드가 있으면 검색, 없으면 전체 목록
        // keyword != null: null 체크 (파라미터가 전달되지 않았을 수 있음)
        // !keyword.trim().isEmpty(): 공백만 있는 경우 제외
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Elasticsearch로 검색 (검색 키워드가 있는 경우)
            // boardService.searchBoards(): Service에서 Elasticsearch 검색 수행
            // keyword.trim(): 앞뒤 공백 제거
            boards = boardService.searchBoards(keyword.trim(), searchType, pageable);
            
            // 검색 키워드와 검색 타입을 뷰에 전달 (검색 폼에 표시하기 위해)
            model.addAttribute("keyword", keyword);      // 뷰에서 ${keyword}로 사용
            model.addAttribute("searchType", searchType); // 뷰에서 ${searchType}로 사용
        } else {
            // 전체 목록 조회 (검색 키워드가 없는 경우)
            // boardService.getAllBoards(): Service에서 MySQL 전체 목록 조회
            boards = boardService.getAllBoards(pageable);
        }
        
        // Model에 데이터 추가 (뷰로 전달)
        // 뷰(HTML)에서 이 데이터들을 사용할 수 있음
        model.addAttribute("boards", boards);                    // 게시글 목록 (Page<BoardResponse>)
        
        /**
         * boards.getNumber(): 현재 페이지 번호 가져오기
         * 
         * 데이터 출처:
         * - Pageable 객체에서 가져온 현재 페이지 번호
         * - URL에서 ?page=2 형태로 전달된 값
         * - 기본값: 0 (첫 페이지)
         * 
         * 반환값:
         * - int 타입: 현재 페이지 번호 (0부터 시작)
         * - 예: 첫 페이지 → 0, 두 번째 페이지 → 1, 세 번째 페이지 → 2
         * 
         * 동작 원리:
         * 1. 사용자가 /boards?page=2 접속
         * 2. Spring이 Pageable 객체 생성 (page=2, size=10)
         * 3. boardService.getAllBoards(pageable) 호출
         * 4. Spring Data JPA가 Page 객체 생성 시 pageable.getPageNumber() 값을 포함
         * 5. boards.getNumber() → 2 반환
         * 
         * Page 인터페이스:
         * - Spring Data의 org.springframework.data.domain.Page 인터페이스
         * - 페이징된 결과를 담는 객체
         * - getNumber() 메서드가 정의되어 있음
         */
        model.addAttribute("currentPage", boards.getNumber());     // 현재 페이지 번호 (0부터 시작)
        
        /**
         * boards.getTotalPages(): 전체 페이지 수 가져오기
         * 
         * 데이터 출처:
         * - Spring Data가 자동으로 계산한 값
         * - 계산 공식: 전체 데이터 개수(totalElements) ÷ 페이지 크기(pageSize)
         * - 예: 전체 100개, 페이지 크기 10 → 전체 페이지 수 = 10
         * 
         * 반환값:
         * - int 타입: 전체 페이지 수
         * - 예: 전체 100개, 페이지 크기 10 → 10페이지
         * - 예: 전체 95개, 페이지 크기 10 → 10페이지 (마지막 페이지는 5개만)
         * 
         * 동작 원리:
         * 1. boardService.getAllBoards(pageable) 호출
         * 2. boardRepository.findAll(pageable) 실행
         * 3. Spring Data JPA가 두 가지 쿼리 실행:
         *    a) 데이터 조회: SELECT * FROM boards LIMIT 10 OFFSET 0
         *    b) 전체 개수 조회: SELECT COUNT(*) FROM boards
         * 4. 전체 개수를 페이지 크기로 나눠서 전체 페이지 수 계산
         *    예: 전체 100개 ÷ 10개 = 10페이지
         * 5. Page 객체에 totalPages 값 포함
         * 6. boards.getTotalPages() → 10 반환
         * 
         * Page 인터페이스:
         * - Spring Data의 org.springframework.data.domain.Page 인터페이스
         * - getTotalPages() 메서드가 정의되어 있음
         * - 내부적으로 getTotalElements() ÷ getSize() 계산
         * 
         * 주의사항:
         * - 전체 개수가 0이면 totalPages = 0
         * - 전체 개수가 페이지 크기보다 작으면 totalPages = 1
         * - 나머지가 있으면 올림 처리 (예: 95개 ÷ 10 = 9.5 → 10페이지)
         */
        model.addAttribute("totalPages", boards.getTotalPages()); // 전체 페이지 수
        
        // 뷰 이름 반환
        // "board/list" → Spring이 templates/board/list.html 파일을 찾아서 렌더링
        // URL은 변경되지 않고 (/boards 유지), 같은 URL에서 HTML만 렌더링됨
        return "board/list";
    }
    
    /**
     * 게시글 상세 조회
     * 
     * @GetMapping("/{id}"): 경로 변수 사용
     *                       - URL: GET /boards/1 (1이 {id}에 들어감)
     *                       - {id}: 경로 변수 (중괄호로 표시)
     *                       - 예: /boards/123 → id = 123
     *                       - @RequestMapping("/boards")와 합쳐져서 최종 URL: /boards/{id}
     * 
     * @PathVariable: URL 경로에서 변수를 추출하는 어노테이션
     *                - URL: /boards/1 → id = 1
     *                - URL: /boards/123 → id = 123
     *                - 경로 변수 vs 쿼리 파라미터:
     *                  * 경로 변수: /boards/1 → @PathVariable
     *                  * 쿼리 파라미터: /boards?id=1 → @RequestParam
     * 
     * @param id 게시글 번호 (Long 타입)
     *           - URL 경로에서 추출된 값
     *           - 예: /boards/1 → id = 1L
     * 
     * @param model 뷰에 데이터를 전달하기 위한 객체
     *              - Spring이 자동으로 생성해서 전달
     * 
     * @return 뷰 이름 (templates/board/detail.html)
     *         - "board/detail": templates/board/detail.html 파일 렌더링
     */
    @GetMapping("/{id}")  // URL: GET /boards/{id} (예: /boards/1)
    public String getBoard(@PathVariable Long id, Model model) {
        // Service에서 게시글 조회
        // boardService.getBoard(id): Service 계층에서 비즈니스 로직 처리
        // BoardResponse: 응답용 DTO (Entity를 직접 반환하지 않음)
        BoardResponse board = boardService.getBoard(id);
        
        // Model에 게시글 정보 추가 (뷰로 전달)
        // 뷰(HTML)에서 ${board}로 사용 가능
        model.addAttribute("board", board);
        
        // 뷰 이름 반환
        // "board/detail" → templates/board/detail.html 렌더링
        return "board/detail";
    }
    
    /**
     * 게시글 작성 폼 페이지
     * 
     * @GetMapping("/new"): HTTP GET 요청 처리
     *                      - URL: GET /boards/new
     *                      - 작성 폼 페이지를 보여주는 역할
     *                      - 실제 저장은 @PostMapping에서 처리
     * 
     * @param model 뷰에 데이터를 전달하기 위한 객체
     * 
     * @return 뷰 이름 (templates/board/form.html)
     *         - "board/form": 작성 폼 HTML 페이지 렌더링
     */
    @GetMapping("/new")  // URL: GET /boards/new
    public String createBoardForm(Model model) {
        // 빈 BoardRequest 객체를 만들어서 폼에 전달
        // new BoardRequest(): 모든 필드가 null인 빈 객체 생성
        // Thymeleaf 템플릿에서 th:object="${board}"로 사용
        // 폼의 input 필드들이 이 객체와 바인딩됨
        model.addAttribute("board", new BoardRequest());
        
        // 뷰 이름 반환
        // "board/form" → templates/board/form.html 렌더링
        return "board/form";
    }
    
    /**
     * 게시글 작성 처리
     * 
     * @PostMapping: HTTP POST 요청을 처리하는 어노테이션
     *               - URL: POST /boards
     *               - 폼에서 submit 버튼을 누르면 이 메서드가 실행됨
     *               - GET vs POST:
     *                 * GET: 데이터 조회 (브라우저 주소창에 표시됨)
     *                 * POST: 데이터 생성/수정/삭제 (브라우저 주소창에 표시 안 됨)
     * 
     * @ModelAttribute: 폼에서 전송된 데이터를 자동으로 객체로 변환하는 어노테이션
     *                  - HTML 폼의 input 필드 이름과 객체의 필드 이름이 일치하면 자동 매핑
     *                  - 예시:
     *                    HTML: <input name="title" value="제목">
     *                    → Spring이 자동으로 request.setTitle("제목") 호출
     *                  - 폼 데이터 → BoardRequest 객체로 자동 변환
     * 
     * @param request 폼에서 전송된 게시글 데이터 (BoardRequest 객체)
     *                - title, content, author 필드가 자동으로 채워짐
     * 
     * @return 리다이렉트 URL
     *         - "redirect:/boards": /boards로 리다이렉트 (URL 변경됨)
     *         - 리다이렉트 vs 뷰 반환:
     *           * return "board/list": 뷰 렌더링 (URL 변경 안 됨, /boards 유지)
     *           * return "redirect:/boards": 다른 URL로 이동 (URL 변경됨, /boards로 이동)
     *         - 작성 완료 후 목록 페이지로 자동 이동 (새로고침 시 중복 저장 방지)
     */
    @PostMapping  // URL: POST /boards (폼 submit 시 실행)
    public String createBoard(@ModelAttribute BoardRequest request) {
        // Service에서 게시글 저장
        // boardService.createBoard(request): 비즈니스 로직 처리
        // - 데이터 검증
        // - Entity로 변환
        // - MySQL에 저장
        // - Elasticsearch 동기화 이벤트 발행
        boardService.createBoard(request);
        
        // 리다이렉트 반환
        // "redirect:/boards": /boards로 리다이렉트
        // 작성 완료 후 목록 페이지로 자동 이동
        // 리다이렉트를 사용하는 이유:
        // - 새로고침 시 중복 저장 방지 (POST 요청이 다시 실행되지 않음)
        // - 사용자가 목록 페이지에서 결과 확인 가능
        return "redirect:/boards";
    }
    
    /**
     * 게시글 수정 폼 페이지
     * 
     * @GetMapping("/{id}/edit"): HTTP GET 요청 처리
     *                            - URL: GET /boards/1/edit
     *                            - 수정 폼 페이지를 보여주는 역할
     *                            - 실제 수정은 @PostMapping("/{id}")에서 처리
     * 
     * @PathVariable: URL 경로에서 변수 추출
     *                - URL: /boards/1/edit → id = 1
     * 
     * @param id 수정할 게시글 번호 (URL 경로에서 추출)
     * 
     * @param model 뷰에 데이터를 전달하기 위한 객체
     * 
     * @return 뷰 이름 (templates/board/form.html)
     *         - "board/form": 작성 폼과 동일한 HTML 사용
     *         - 작성 폼과 수정 폼을 하나의 템플릿으로 재사용
     */
    @GetMapping("/{id}/edit")  // URL: GET /boards/{id}/edit (예: /boards/1/edit)
    public String updateBoardForm(@PathVariable Long id, Model model) {
        // 기존 게시글 정보 조회
        // boardService.getBoard(id): Service에서 게시글 조회
        // BoardResponse: 응답용 DTO (조회 결과)
        BoardResponse board = boardService.getBoard(id);
        
        // BoardResponse를 BoardRequest로 변환
        // 이유: 폼은 BoardRequest를 사용하므로 변환 필요
        // BoardResponse: 조회용 DTO (모든 필드 포함)
        // BoardRequest: 입력용 DTO (title, content, author만 포함)
        BoardRequest request = new BoardRequest();
        request.setTitle(board.getTitle());      // 제목 복사
        request.setContent(board.getContent());  // 내용 복사
        request.setAuthor(board.getAuthor());   // 작성자 복사
        
        // Model에 데이터 추가 (뷰로 전달)
        model.addAttribute("board", request);  // 폼에 표시할 데이터
        model.addAttribute("id", id);           // 수정 폼에서 사용할 게시글 번호
        // 뷰에서 id를 사용하여 수정 폼인지 작성 폼인지 구분 가능
        
        // 뷰 이름 반환
        // "board/form" → templates/board/form.html 렌더링
        // 작성 폼과 동일한 템플릿 사용 (코드 재사용)
        return "board/form";
    }
    
    /**
     * 게시글 수정 처리
     * 
     * @PostMapping("/{id}"): HTTP POST 요청 처리
     *                        - URL: POST /boards/1
     *                        - 수정 폼에서 submit하면 이 메서드 실행
     *                        - 작성 처리(@PostMapping)와 다른 URL로 구분
     * 
     * @PathVariable: URL 경로에서 변수 추출
     *                - URL: /boards/1 → id = 1
     * 
     * @ModelAttribute: 폼에서 전송된 데이터를 자동으로 객체로 변환
     *                  - 수정 폼의 input 필드들이 BoardRequest 객체로 변환됨
     * 
     * @param id 수정할 게시글 번호 (URL 경로에서 추출)
     * 
     * @param request 수정할 내용 (폼에서 전송된 데이터)
     *                - title, content 필드가 자동으로 채워짐
     * 
     * @return 리다이렉트 URL
     *         - "redirect:/boards/" + id: 수정 후 상세 페이지로 이동
     *         - 예: id = 1 → "redirect:/boards/1"
     *         - 수정 완료 후 상세 페이지에서 결과 확인 가능
     */
    @PostMapping("/{id}")  // URL: POST /boards/{id} (예: POST /boards/1)
    public String updateBoard(@PathVariable Long id, @ModelAttribute BoardRequest request) {
        // Service에서 게시글 수정
        // boardService.updateBoard(id, request): 비즈니스 로직 처리
        // - 기존 게시글 조회
        // - 데이터 수정
        // - MySQL에 저장
        // - Elasticsearch 동기화 이벤트 발행
        boardService.updateBoard(id, request);
        
        // 리다이렉트 반환
        // "redirect:/boards/" + id: 수정 후 상세 페이지로 이동
        // 예: id = 1 → "redirect:/boards/1"
        // 사용자가 수정된 내용을 상세 페이지에서 확인 가능
        return "redirect:/boards/" + id;
    }
    
    /**
     * 게시글 삭제 처리
     * 
     * @PostMapping("/{id}/delete"): HTTP POST 요청 처리
     *                                - URL: POST /boards/1/delete
     *                                - 삭제 버튼을 누르면 이 메서드 실행
     *                                - 삭제는 위험한 작업이므로 POST 메서드 사용
     *                                  (GET으로 하면 URL만으로 삭제 가능해져서 위험)
     * 
     * @PathVariable: URL 경로에서 변수 추출
     *                - URL: /boards/1/delete → id = 1
     * 
     * @param id 삭제할 게시글 번호 (URL 경로에서 추출)
     * 
     * @return 리다이렉트 URL
     *         - "redirect:/boards": 삭제 후 목록 페이지로 이동
     *         - 삭제된 게시글은 더 이상 존재하지 않으므로 상세 페이지로 이동 불가
     *         - 목록 페이지에서 삭제 결과 확인 가능
     */
    @PostMapping("/{id}/delete")  // URL: POST /boards/{id}/delete (예: POST /boards/1/delete)
    public String deleteBoard(@PathVariable Long id) {
        // Service에서 게시글 삭제
        // boardService.deleteBoard(id): 비즈니스 로직 처리
        // - 게시글 존재 여부 확인
        // - MySQL에서 삭제
        // - Elasticsearch 동기화 이벤트 발행
        boardService.deleteBoard(id);
        
        // 리다이렉트 반환
        // "redirect:/boards": 삭제 후 목록 페이지로 이동
        // 삭제된 게시글은 더 이상 존재하지 않으므로 상세 페이지로 이동할 수 없음
        // 목록 페이지에서 삭제 결과 확인 가능
        return "redirect:/boards";
    }
}

