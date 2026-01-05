package com.example.board.elasticsearch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BoardSearchRepository: Elasticsearch 데이터 접근 계층
 * 
 * ElasticsearchRepository<BoardDocument, Long>:
 * - Spring Data Elasticsearch가 제공하는 인터페이스
 * - 기본적인 CRUD 메서드 자동 제공
 * 
 * 제네릭 타입:
 * - BoardDocument: Elasticsearch 문서 타입
 * - Long: 문서 ID 타입
 * 
 * 자동으로 제공되는 메서드:
 * - save(BoardDocument): 문서 저장/업데이트
 * - findById(Long): ID로 문서 조회
 * - deleteById(Long): ID로 문서 삭제
 * - findAll(): 모든 문서 조회
 */
@Repository
public interface BoardSearchRepository extends ElasticsearchRepository<BoardDocument, Long> {
    
    /**
     * 제목으로 검색 (전문 검색, 페이징)
     * - Spring Data Elasticsearch가 메서드 이름을 분석해서 자동으로 쿼리 생성
     * - findByTitleContaining → title 필드에서 키워드 포함 검색
     * 
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 검색 결과
     */
    Page<BoardDocument> findByTitleContaining(String keyword, Pageable pageable);
    
    /**
     * 내용으로 검색 (전문 검색, 페이징)
     * 
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 검색 결과
     */
    Page<BoardDocument> findByContentContaining(String keyword, Pageable pageable);
    
    /**
     * 제목 또는 내용으로 검색 (전문 검색, 페이징)
     * - title과 content 필드 모두에서 검색
     * - @Query: Elasticsearch 쿼리를 직접 지정
     * 
     * @param keyword 검색 키워드
     * @param pageable 페이징 정보
     * @return 페이징된 검색 결과
     */
    @Query("{\"bool\": {\"should\": [{\"match\": {\"title\": \"?0\"}}, {\"match\": {\"content\": \"?0\"}}]}}")
    Page<BoardDocument> findByTitleContainingOrContentContaining(
            String keyword, Pageable pageable);
    
    /**
     * 작성자로 검색 (정확한 일치, 페이징)
     * - Keyword 타입이므로 정확한 일치 검색
     * 
     * @param author 작성자
     * @param pageable 페이징 정보
     * @return 페이징된 검색 결과
     */
    Page<BoardDocument> findByAuthor(String author, Pageable pageable);
}

