package com.example.board.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * BoardSyncEvent: 게시글 동기화 이벤트
 * 
 * 이벤트 기반 아키텍처에서 사용하는 이벤트 클래스
 * - 게시글이 생성/수정/삭제될 때 이벤트를 발행
 * - 스케줄러 워커가 이 이벤트를 처리해서 Elasticsearch에 동기화
 * 
 * 이벤트 타입:
 * - CREATE: 게시글 생성
 * - UPDATE: 게시글 수정
 * - DELETE: 게시글 삭제
 */
@Getter
@RequiredArgsConstructor
public class BoardSyncEvent {
    
    /**
     * 이벤트 타입
     */
    public enum EventType {
        CREATE,  // 게시글 생성
        UPDATE,  // 게시글 수정
        DELETE   // 게시글 삭제
    }
    
    /**
     * 게시글 ID
     */
    private final Long boardId;
    
    /**
     * 이벤트 타입
     */
    private final EventType eventType;
    
    /**
     * 이벤트 발생 시간 (밀리초)
     */
    private final long timestamp;
    
    /**
     * 생성자
     * 
     * @param boardId 게시글 ID
     * @param eventType 이벤트 타입
     */
    public BoardSyncEvent(Long boardId, EventType eventType) {
        this.boardId = boardId;
        this.eventType = eventType;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 게시글 생성 이벤트 생성
     */
    public static BoardSyncEvent create(Long boardId) {
        return new BoardSyncEvent(boardId, EventType.CREATE);
    }
    
    /**
     * 게시글 수정 이벤트 생성
     */
    public static BoardSyncEvent update(Long boardId) {
        return new BoardSyncEvent(boardId, EventType.UPDATE);
    }
    
    /**
     * 게시글 삭제 이벤트 생성
     */
    public static BoardSyncEvent delete(Long boardId) {
        return new BoardSyncEvent(boardId, EventType.DELETE);
    }
}

