package com.example.board.dto;

// Lombok: 코드 자동 생성 라이브러리
import lombok.AllArgsConstructor;  // 모든 필드를 파라미터로 받는 생성자 자동 생성
import lombok.Getter;              // 모든 필드의 getter 메서드 자동 생성
import lombok.NoArgsConstructor;   // 기본 생성자 자동 생성
import lombok.Setter;             // 모든 필드의 setter 메서드 자동 생성

/**
 * BoardRequest: 게시글 작성/수정 요청 DTO (Data Transfer Object)
 * 
 * DTO란?
 * - Data Transfer Object: 계층 간 데이터 전송을 위한 객체
 * - Entity를 직접 사용하지 않고 DTO를 사용하는 이유:
 *   1. 보안: Entity의 모든 필드를 노출하지 않음
 *           - 예: Entity에 비밀번호 필드가 있어도 DTO에는 없음
 *   2. 유연성: 필요한 데이터만 전송
 *           - 요청에 필요한 필드만 포함
 *           - 예: 작성 시에는 id가 필요 없음
 *   3. 계층 분리: 각 계층의 책임을 명확히 구분
 *           - Controller는 DTO만 다루고 Entity는 모름
 *           - Service는 Entity와 DTO를 변환
 * 
 * 사용 시점:
 * - Controller → Service로 데이터를 전달할 때 사용
 * - 웹 요청(HTTP)에서 받은 데이터를 담는 그릇
 * - 예: 폼에서 제출한 데이터를 BoardRequest 객체로 받음
 * 
 * 필드 설명:
 * - title, content, author만 포함
 * - id는 포함하지 않음 (자동 생성되므로)
 * - createdAt, updatedAt은 포함하지 않음 (자동 설정되므로)
 * 
 * Lombok 어노테이션 설명:
 * 
 * @Getter: 모든 필드의 getter 메서드 자동 생성
 *          - 예: public String getTitle() { return title; }
 *          - 필드에 직접 접근하지 않고 getter를 통해 접근
 * 
 * @Setter: 모든 필드의 setter 메서드 자동 생성
 *          - 예: public void setTitle(String title) { this.title = title; }
 *          - Spring이 폼 데이터를 객체로 변환할 때 사용
 *          - 예: @ModelAttribute BoardRequest request
 * 
 * @NoArgsConstructor: 기본 생성자 자동 생성 (파라미터 없음)
 *                     - 예: public BoardRequest() { }
 *                     - Spring이 객체를 생성할 때 필요
 * 
 * @AllArgsConstructor: 모든 필드를 파라미터로 받는 생성자 자동 생성
 *                      - 예: public BoardRequest(String title, String content, String author)
 *                      - 객체를 한 번에 생성할 때 편리함
 * 
 * 사용 예시:
 * // Controller에서
 * @PostMapping
 * public String createBoard(@ModelAttribute BoardRequest request) {
 *     // 폼 데이터가 자동으로 BoardRequest 객체로 변환됨
 *     // request.getTitle(), request.getContent() 등 사용 가능
 *     boardService.createBoard(request);
 * }
 * 
 * // Service에서
 * Board board = Board.builder()
 *     .title(request.getTitle())      // DTO에서 값 가져오기
 *     .content(request.getContent())
 *     .author(request.getAuthor())
 *     .build();
 */
@Getter              // 모든 필드의 getter 메서드 자동 생성
@Setter              // 모든 필드의 setter 메서드 자동 생성
@NoArgsConstructor   // 기본 생성자 자동 생성
@AllArgsConstructor  // 모든 필드를 파라미터로 받는 생성자 자동 생성
public class BoardRequest {
    /**
     * 게시글 제목
     * - 사용자가 입력한 제목
     * - 최대 길이는 Entity의 @Column(length = 500)에 따라 500자
     */
    private String title;    // 게시글 제목
    
    /**
     * 게시글 내용
     * - 사용자가 입력한 내용
     * - 길이 제한 없음 (Entity에서 TEXT 타입 사용)
     */
    private String content;  // 게시글 내용
    
    /**
     * 작성자
     * - 게시글을 작성한 사람의 이름
     * - 최대 길이는 Entity의 @Column(length = 50)에 따라 50자
     */
    private String author;   // 작성자
}

