package com.example.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HomeController: 홈 페이지 처리
 * 
 * 루트 URL(/)로 접속했을 때 게시판 목록으로 리다이렉트
 */
@Controller
public class HomeController {
    
    /**
     * 루트 URL 처리
     * 
     * @GetMapping("/"): URL: GET /
     * 
     * @return 리다이렉트 (게시판 목록으로 이동)
     */
    @GetMapping("/")
    public String home() {
        // "/boards"로 리다이렉트
        return "redirect:/boards";
    }
}

