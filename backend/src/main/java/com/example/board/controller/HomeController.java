package com.example.board.controller;

// Spring MVC: 웹 요청 처리
import org.springframework.stereotype.Controller;  // 컨트롤러 표시 어노테이션
import org.springframework.web.bind.annotation.GetMapping;  // HTTP GET 요청 처리 어노테이션

/**
 * HomeController: 홈 페이지 처리
 * 
 * 역할:
 * - 루트 URL(/)로 접속했을 때 게시판 목록으로 리다이렉트
 * - 사용자가 http://localhost:8081/ 로 접속하면
 *   자동으로 http://localhost:8081/boards 로 이동
 * 
 * 왜 필요한가?
 * - 사용자 편의성: 루트 URL로 접속해도 바로 게시판을 볼 수 있음
 * - 기본 진입점 제공: 애플리케이션의 시작점 역할
 * 
 * @Controller: 이 클래스가 Controller 계층임을 표시
 *              - Spring MVC가 이 클래스를 인식하고 웹 요청을 처리
 *              - HTML 뷰를 반환 (Thymeleaf 템플릿 사용)
 */
@Controller  // 컨트롤러 표시
public class HomeController {
    
    /**
     * 루트 URL 처리
     * 
     * @GetMapping("/"): HTTP GET 요청 처리
     *                   - URL: GET /
     *                   - 브라우저에서 http://localhost:8081/ 로 접속 시 실행
     * 
     * 리다이렉트:
     * - "redirect:/boards": /boards로 리다이렉트
     * - URL이 변경됨: / → /boards
     * - 브라우저가 자동으로 /boards로 이동
     * 
     * @return 리다이렉트 URL
     *         - "redirect:/boards": 게시판 목록 페이지로 이동
     *         - 리다이렉트 vs 뷰 반환:
     *           * return "board/list": 뷰 렌더링 (URL 변경 안 됨, / 유지)
     *           * return "redirect:/boards": 다른 URL로 이동 (URL 변경됨, /boards로 이동)
     * 
     * 동작 흐름:
     * 1. 사용자가 http://localhost:8081/ 접속
     * 2. 이 메서드 실행
     * 3. "redirect:/boards" 반환
     * 4. 브라우저가 자동으로 http://localhost:8081/boards 로 이동
     * 5. BoardController의 getAllBoards() 메서드 실행
     */
    @GetMapping("/")  // URL: GET / (루트 URL)
    public String home() {
        // "/boards"로 리다이렉트
        // 사용자가 루트 URL로 접속하면 게시판 목록으로 자동 이동
        return "redirect:/boards";
    }
}

