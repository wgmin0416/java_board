package com.example.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * BoardRequest: 게시글 작성/수정 요청 DTO (Data Transfer Object)
 * 
 * DTO란?
 * - 계층 간 데이터 전송을 위한 객체
 * - Entity를 직접 사용하지 않고 DTO를 사용하는 이유:
 *   1. 보안: Entity의 모든 필드를 노출하지 않음
 *   2. 유연성: 필요한 데이터만 전송
 *   3. 계층 분리: 각 계층의 책임을 명확히 구분
 * 
 * 예를 들어:
 * - Controller → Service로 데이터를 전달할 때 사용
 * - 웹 요청에서 받은 데이터를 담는 그릇
 * 
 * @Getter, @Setter: Lombok이 getter/setter 메서드를 자동 생성
 * @NoArgsConstructor: 기본 생성자 (파라미터 없음)
 * @AllArgsConstructor: 모든 필드를 파라미터로 받는 생성자
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequest {
    private String title;    // 게시글 제목
    private String content;  // 게시글 내용
    private String author;   // 작성자
}

