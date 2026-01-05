package com.example.board.dto;

import com.example.board.entity.Board;
import com.example.board.elasticsearch.BoardDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * BoardResponse: 게시글 조회 응답 DTO
 * 
 * Response DTO의 역할:
 * - Service → Controller로 데이터를 전달할 때 사용
 * - Entity를 그대로 반환하지 않고 DTO로 변환해서 반환
 * - 필요한 정보만 선택적으로 전달 가능
 * 
 * @Builder: 빌더 패턴으로 객체 생성
 *   예: BoardResponse.builder()
 *          .id(1L)
 *          .title("제목")
 *          .build()
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardResponse {
    private Long id;                    // 게시글 번호
    private String title;               // 제목
    private String content;             // 내용
    private String author;              // 작성자
    private LocalDateTime createdAt;    // 작성일시
    private LocalDateTime updatedAt;    // 수정일시
    
    /**
     * Entity를 Response DTO로 변환하는 정적 메서드
     * 
     * 정적 메서드(static method)란?
     * - 객체를 생성하지 않고도 호출할 수 있는 메서드
     * - 클래스 이름으로 직접 호출: BoardResponse.from(board)
     * 
     * 왜 이렇게 하나?
     * - Entity의 모든 필드를 DTO로 복사하는 작업을 한 곳에서 관리
     * - 변환 로직이 변경되어도 이 메서드만 수정하면 됨
     * 
     * @param board 변환할 Board 엔티티
     * @return BoardResponse DTO 객체
     */
    public static BoardResponse from(Board board) {
        return BoardResponse.builder()
                .id(board.getId())              // Entity의 id를 DTO의 id로 복사
                .title(board.getTitle())         // Entity의 title을 DTO의 title로 복사
                .content(board.getContent())     // Entity의 content를 DTO의 content로 복사
                .author(board.getAuthor())      // Entity의 author를 DTO의 author로 복사
                .createdAt(board.getCreatedAt()) // Entity의 createdAt을 DTO의 createdAt으로 복사
                .updatedAt(board.getUpdatedAt()) // Entity의 updatedAt을 DTO의 updatedAt으로 복사
                .build();                        // Builder 패턴으로 객체 생성
    }
    
    /**
     * Elasticsearch Document를 Response DTO로 변환하는 정적 메서드
     * 
     * 검색 기능에서 Elasticsearch의 검색 결과를 직접 사용하기 위해 추가
     * 
     * @param document 변환할 BoardDocument
     * @return BoardResponse DTO 객체
     */
    public static BoardResponse from(BoardDocument document) {
        return BoardResponse.builder()
                .id(document.getId())
                .title(document.getTitle())
                .content(document.getContent())
                .author(document.getAuthor())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

