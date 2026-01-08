package com.example.board.worker;

import com.example.board.entity.Board;
import com.example.board.elasticsearch.BoardDocument;
import com.example.board.elasticsearch.BoardSearchRepository;
import com.example.board.event.BoardSyncEvent;
import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * BoardSyncWorker: 게시글 동기화 워커
 * 
 * 스케줄러 기반 워커
 * - 30초마다 실행되어 이벤트 큐를 확인
 * - 큐에 있는 이벤트를 처리해서 Elasticsearch에 동기화
 * 
 * @Scheduled: Spring의 스케줄링 기능
 * - fixedRate: 고정 간격으로 실행
 * - fixedDelay: 이전 실행 완료 후 지정된 시간 후 실행
 * - cron: Cron 표현식 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BoardSyncWorker {
    
    private final BoardSyncEventQueue eventQueue;
    private final BoardRepository boardRepository;
    private final BoardSearchRepository boardSearchRepository;
    
    /**
     * 30초마다 실행되는 동기화 작업
     * 
     * @Scheduled(fixedRate = 30000): 30초(30000ms)마다 실행
     * - fixedRate: 이전 실행 시작 시간 기준으로 다음 실행
     * - 이전 작업이 30초 이상 걸리면 바로 다음 작업 시작
     * 
     * 동작 방식:
     * 1. 이벤트 큐에서 모든 이벤트를 가져옴 (배치 처리)
     * 2. 각 이벤트를 처리 (CREATE, UPDATE, DELETE)
     * 3. Elasticsearch에 동기화
     */
    @Scheduled(fixedRate = 30000)  // 30초마다 실행
    public void syncBoards() {
        // 큐가 비어있으면 처리할 것이 없음
        if (eventQueue.isEmpty()) {
            log.debug("동기화할 이벤트가 없습니다.");
            return;
        }
        
        log.info("게시글 동기화 시작 - 큐에 {}개의 이벤트가 있습니다.", eventQueue.size());
        
        // 큐에서 모든 이벤트를 가져옴 (배치 처리)
        List<BoardSyncEvent> events = eventQueue.drainAll();
        
        int successCount = 0;
        int failCount = 0;
        
        // 각 이벤트 처리
        for (BoardSyncEvent event : events) {
            try {
                processEvent(event);
                successCount++;
            } catch (Exception e) {
                log.error("이벤트 처리 실패: boardId={}, eventType={}, error={}", 
                        event.getBoardId(), event.getEventType(), e.getMessage(), e);
                failCount++;
                // 실패한 이벤트는 다시 큐에 추가할 수도 있음 (선택사항)
            }
        }
        
        log.info("게시글 동기화 완료 - 성공: {}, 실패: {}", successCount, failCount);
    }
    
    /**
     * 개별 이벤트 처리
     * 
     * @param event 동기화 이벤트
     */
    private void processEvent(BoardSyncEvent event) {
        Long boardId = event.getBoardId();
        BoardSyncEvent.EventType eventType = event.getEventType();
        
        switch (eventType) {
            case CREATE:
            case UPDATE:
                // CREATE 또는 UPDATE: MySQL에서 데이터를 가져와서 Elasticsearch에 저장
                syncBoardToElasticsearch(boardId);
                break;
                
            case DELETE:
                // DELETE: Elasticsearch에서 삭제
                deleteBoardFromElasticsearch(boardId);
                break;
                
            default:
                log.warn("알 수 없는 이벤트 타입: {}", eventType);
        }
    }
    
    /**
     * MySQL에서 게시글을 가져와서 Elasticsearch에 동기화
     * 
     * @param boardId 게시글 ID
     */
    private void syncBoardToElasticsearch(Long boardId) {
        try {
            // MySQL에서 게시글 조회
            Board board = boardRepository.findById(boardId)
                    .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다: " + boardId));
            
            // Board Entity를 BoardDocument로 변환
            BoardDocument document = BoardDocument.builder()
                    .id(board.getId())
                    .title(board.getTitle())
                    .content(board.getContent())
                    .author(board.getAuthor())
                    .createdAt(board.getCreatedAt())
                    .updatedAt(board.getUpdatedAt())
                    .build();
            
            // Elasticsearch에 저장 (없으면 생성, 있으면 업데이트)
            boardSearchRepository.save(document);
            
            log.info("Elasticsearch 동기화 완료: boardId={}, title={}", boardId, board.getTitle());
        } catch (Exception e) {
            log.error("Elasticsearch 동기화 실패: boardId={}, error={}", boardId, e.getMessage(), e);
            throw e; // 상위로 예외 전파
        }
    }
    
    /**
     * Elasticsearch에서 게시글 삭제
     * 
     * @param boardId 게시글 ID
     */
    private void deleteBoardFromElasticsearch(Long boardId) {
        try {
            boardSearchRepository.deleteById(boardId);
            log.info("Elasticsearch에서 삭제 완료: boardId={}", boardId);
        } catch (Exception e) {
            log.error("Elasticsearch 삭제 실패: boardId={}, error={}", boardId, e.getMessage(), e);
            throw e; // 상위로 예외 전파
        }
    }
}

