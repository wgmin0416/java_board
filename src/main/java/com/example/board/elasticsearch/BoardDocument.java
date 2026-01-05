package com.example.board.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

/**
 * BoardDocument: Elasticsearch에 저장될 게시글 문서
 * 
 * Elasticsearch는 NoSQL 문서 기반 검색 엔진
 * - MySQL의 Board 엔티티와 유사하지만 검색 최적화된 구조
 * - @Document: Elasticsearch 인덱스와 매핑
 * 
 * 인덱스(Index): Elasticsearch의 데이터베이스 개념
 * - boards: 인덱스 이름
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "boards")
public class BoardDocument {
    
    /**
     * @Id: Elasticsearch 문서 ID
     * - MySQL의 Board.id와 동일한 값 사용
     */
    @Id
    private Long id;
    
    /**
     * @Field: 필드 타입 지정
     * - type = FieldType.Text: 전문 검색 가능 (분석기 사용)
     * - analyzer = "standard": 표준 분석기 사용
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;
    
    /**
     * 내용 필드
     * - 전문 검색 가능
     */
    @Field(type = FieldType.Text, analyzer = "standard")
    private String content;
    
    /**
     * 작성자 필드
     * - type = FieldType.Keyword: 정확한 일치 검색 (분석 안 함)
     */
    @Field(type = FieldType.Keyword)
    private String author;
    
    /**
     * 작성일시
     * - type = FieldType.Date: 날짜 타입
     */
    @Field(type = FieldType.Date)
    private LocalDateTime createdAt;
    
    /**
     * 수정일시
     */
    @Field(type = FieldType.Date)
    private LocalDateTime updatedAt;
}

