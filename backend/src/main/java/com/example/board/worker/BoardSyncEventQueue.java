package com.example.board.worker;

import com.example.board.event.BoardSyncEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * BoardSyncEventQueue: 게시글 동기화 이벤트 큐 관리
 * 
 * 이벤트 기반 아키텍처에서 이벤트를 임시 저장하는 큐
 * - 게시글이 생성/수정/삭제될 때 이벤트를 큐에 추가
 * - 스케줄러 워커가 주기적으로 큐를 확인하고 처리
 * 
 * Thread-safe: 여러 스레드에서 동시에 접근해도 안전
 * - BlockingQueue 사용 (동시성 제어 내장)
 */
@Slf4j
@Component
public class BoardSyncEventQueue {
    
    /**
     * 이벤트 큐
     * - LinkedBlockingQueue: 스레드 안전한 큐
     * - 무제한 크기 (필요시 크기 제한 가능)
     */
    private final BlockingQueue<BoardSyncEvent> eventQueue = new LinkedBlockingQueue<>();
    
    /**
     * 이벤트를 큐에 추가
     * 
     * @param event 동기화 이벤트
     */
    public void addEvent(BoardSyncEvent event) {
        eventQueue.offer(event);
        log.debug("이벤트 큐에 추가: boardId={}, eventType={}", 
                event.getBoardId(), event.getEventType());
    }
    
    /**
     * 큐에서 이벤트를 가져옴 (큐가 비어있으면 null 반환)
     * 
     * @return 이벤트 또는 null
     */
    public BoardSyncEvent pollEvent() {
        return eventQueue.poll();
    }
    
    /**
     * 큐에 남아있는 이벤트 개수
     * 
     * @return 이벤트 개수
     */
    public int size() {
        return eventQueue.size();
    }
    
    /**
     * 큐가 비어있는지 확인
     * 
     * @return 비어있으면 true
     */
    public boolean isEmpty() {
        return eventQueue.isEmpty();
    }
    
    /**
     * 큐의 모든 이벤트를 가져옴 (배치 처리용)
     * 
     * @return 이벤트 리스트
     */
    public java.util.List<BoardSyncEvent> drainAll() {
        java.util.List<BoardSyncEvent> events = new java.util.ArrayList<>();
        eventQueue.drainTo(events);
        return events;
    }
}

