package com.example.board.dto;

// 엔티티와 Elasticsearch 문서
import com.example.board.entity.Board;              // MySQL 엔티티
import com.example.board.elasticsearch.BoardDocument; // Elasticsearch 문서
// Lombok: 코드 자동 생성 라이브러리
import lombok.AllArgsConstructor;  // 모든 필드를 파라미터로 받는 생성자 자동 생성
import lombok.Builder;            // 빌더 패턴 자동 생성
import lombok.Getter;             // 모든 필드의 getter 메서드 자동 생성
import lombok.NoArgsConstructor;   // 기본 생성자 자동 생성

// Java 8의 날짜/시간 API
import java.time.LocalDateTime;   // 날짜+시간을 표현하는 클래스

/**
 * BoardResponse: 게시글 조회 응답 DTO
 * 
 * Response DTO의 역할:
 * - Service → Controller로 데이터를 전달할 때 사용
 * - Entity를 그대로 반환하지 않고 DTO로 변환해서 반환
 * - 필요한 정보만 선택적으로 전달 가능
 * 
 * 왜 Entity를 직접 반환하지 않나?
 * 1. 보안: Entity의 모든 필드를 노출하지 않음
 * 2. 유연성: 필요한 데이터만 선택적으로 전달
 * 3. 순환 참조 방지: Entity 간의 관계가 복잡할 때 문제 방지
 * 4. API 버전 관리: DTO를 변경해도 Entity는 변경하지 않아도 됨
 * 
 * @Builder: 빌더 패턴으로 객체 생성
 *   - 객체를 생성할 때 필드를 하나씩 설정하면서 만들 수 있음
 *   - 예: BoardResponse.builder()
 *              .id(1L)
 *              .title("제목")
 *              .content("내용")
 *              .build()
 *   - 장점: 가독성이 좋고, 필드를 선택적으로 설정 가능
 * 
 * 필드 설명:
 * - Entity의 모든 필드를 포함 (id, title, content, author, createdAt, updatedAt)
 * - 조회 결과를 그대로 전달하기 위해 모든 필드 포함
 */
@Getter              // 모든 필드의 getter 메서드 자동 생성
@NoArgsConstructor   // 기본 생성자 자동 생성
@AllArgsConstructor  // 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder             // 빌더 패턴 자동 생성
public class BoardResponse {
    /**
     * 게시글 번호 (기본키)
     * - 데이터베이스에서 자동 생성된 번호
     * - Long 타입: 64비트 정수
     */
    private Long id;                    // 게시글 번호
    
    /**
     * 게시글 제목
     * - 사용자가 입력한 제목
     */
    private String title;               // 제목
    
    /**
     * 게시글 내용
     * - 사용자가 입력한 내용
     */
    private String content;             // 내용
    
    /**
     * 작성자
     * - 게시글을 작성한 사람의 이름
     */
    private String author;              // 작성자
    
    /**
     * 작성일시
     * - 게시글이 처음 작성된 시간
     * - LocalDateTime 타입: 날짜+시간 (예: 2024-01-01T12:30:45)
     * - @PrePersist에서 자동 설정됨
     */
    private LocalDateTime createdAt;    // 작성일시
    
    /**
     * 수정일시
     * - 게시글이 마지막으로 수정된 시간
     * - LocalDateTime 타입: 날짜+시간
     * - @PreUpdate에서 자동 갱신됨
     * - 처음 작성 시에는 작성일시와 동일
     */
    private LocalDateTime updatedAt;    // 수정일시
    
    /**
     * Entity를 Response DTO로 변환하는 정적 메서드
     * 
     * 정적 메서드(static method)란?
     * - 객체를 생성하지 않고도 호출할 수 있는 메서드
     * - 클래스 이름으로 직접 호출: BoardResponse.from(board)
     * - 예: BoardResponse response = BoardResponse.from(board);
     * 
     * 왜 정적 메서드로 만들었나?
     * - 변환 로직을 한 곳에서 관리
     * - 변환 로직이 변경되어도 이 메서드만 수정하면 됨
     * - 코드 재사용성 향상
     * 
     * 왜 이렇게 하나?
     * - Entity의 모든 필드를 DTO로 복사하는 작업을 한 곳에서 관리
     * - 변환 로직이 변경되어도 이 메서드만 수정하면 됨
     * - 여러 곳에서 사용할 수 있음 (Service, Controller 등)
     * 
     * Builder 패턴 사용:
     * - BoardResponse.builder()로 빌더 객체 생성
     * - 각 필드를 메서드 체이닝으로 설정
     * - .build()로 최종 객체 생성
     * 
     * @param board 변환할 Board 엔티티 (MySQL에서 조회한 데이터)
     * @return BoardResponse DTO 객체 (뷰에 전달할 데이터)
     * 
     * 사용 예시:
     * Board board = boardRepository.findById(1L).get();
     * BoardResponse response = BoardResponse.from(board);
     * // response를 뷰에 전달
     */
    public static BoardResponse from(Board board) {
        // Builder 패턴으로 BoardResponse 객체 생성
        return BoardResponse.builder()
                .id(board.getId())              // Entity의 id를 DTO의 id로 복사
                .title(board.getTitle())         // Entity의 title을 DTO의 title로 복사
                .content(board.getContent())     // Entity의 content를 DTO의 content로 복사
                .author(board.getAuthor())      // Entity의 author를 DTO의 author로 복사
                .createdAt(board.getCreatedAt()) // Entity의 createdAt을 DTO의 createdAt으로 복사
                .updatedAt(board.getUpdatedAt()) // Entity의 updatedAt을 DTO의 updatedAt으로 복사
                .build();                        // Builder 패턴으로 최종 객체 생성
    }
    
    /**
     * Elasticsearch Document를 Response DTO로 변환하는 정적 메서드
     * 
     * 메서드 오버로딩:
     * - 같은 이름의 메서드지만 파라미터 타입이 다름
     * - from(Board)와 from(BoardDocument)는 다른 메서드
     * - Java가 파라미터 타입을 보고 적절한 메서드를 호출
     * 
     * Elasticsearch에서 검색된 결과를 바로 사용할 때 사용:
     * - MySQL 조회 없이 Elasticsearch 결과를 직접 사용
     * - 더 빠른 응답 시간 (데이터베이스 조회 1회 감소)
     * - 검색 기능에서 사용됨
     * 
     * 동작 원리:
     * 1. Elasticsearch에서 검색 (BoardDocument 조회)
     * 2. BoardDocument를 BoardResponse로 변환 (이 메서드 사용)
     * 3. 변환된 BoardResponse를 뷰에 전달
     * 
     * @param document 변환할 BoardDocument (Elasticsearch에서 조회한 데이터)
     * @return BoardResponse DTO 객체 (뷰에 전달할 데이터)
     * 
     * 사용 예시:
     * Page<BoardDocument> documents = boardSearchRepository.findByTitleContaining("키워드", pageable);
     * List<BoardResponse> responses = documents.getContent().stream()
     *     .map(BoardResponse::from)  // 각 BoardDocument를 BoardResponse로 변환
     *     .collect(Collectors.toList());
     */
    public static BoardResponse from(BoardDocument document) {
        // Builder 패턴으로 BoardResponse 객체 생성
        // BoardDocument의 필드를 BoardResponse로 복사
        return BoardResponse.builder()
                .id(document.getId())              // Document의 id를 DTO의 id로 복사
                .title(document.getTitle())         // Document의 title을 DTO의 title로 복사
                .content(document.getContent())     // Document의 content를 DTO의 content로 복사
                .author(document.getAuthor())      // Document의 author를 DTO의 author로 복사
                .createdAt(document.getCreatedAt()) // Document의 createdAt을 DTO의 createdAt으로 복사
                .updatedAt(document.getUpdatedAt()) // Document의 updatedAt을 DTO의 updatedAt으로 복사
                .build();                           // Builder 패턴으로 최종 객체 생성
    }
}

