package com.example.board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Board: 게시판 엔티티 클래스
 * 
 * 엔티티(Entity)란?
 * - 데이터베이스의 테이블과 Java 클래스를 연결하는 역할
 * - 이 클래스를 보고 JPA가 자동으로 데이터베이스 테이블을 생성하거나 매핑합니다
 * 
 * @Entity: 이 클래스가 JPA 엔티티임을 표시
 *          JPA가 이 클래스를 인식하고 데이터베이스와 연결합니다
 * 
 * @Table: 데이터베이스 테이블 이름 지정
 *         name = "boards" → 데이터베이스에 "boards" 테이블로 생성됨
 */
@Entity
@Table(name = "boards")
@Getter                    // Lombok: 모든 필드의 getter 메서드 자동 생성
@NoArgsConstructor         // Lombok: 기본 생성자 자동 생성 (JPA에서 필요)
@AllArgsConstructor       // Lombok: 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder                  // Lombok: 빌더 패턴 자동 생성 (객체 생성 시 편리함)
public class Board {
    
    /**
     * @Id: 이 필드가 기본키(Primary Key)임을 표시
     * 
     * @GeneratedValue: 기본키 자동 생성 전략
     *   strategy = GenerationType.IDENTITY
     *   - MySQL의 AUTO_INCREMENT와 같은 역할
     *   - 데이터베이스가 자동으로 번호를 증가시켜서 할당
     *   - 예: 1, 2, 3, 4, ...
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // 게시글 번호 (자동 증가)
    
    /**
     * @Column: 데이터베이스 컬럼 설정
     *   nullable = false: NULL 값 허용 안 함 (필수 입력)
     *   length = 500: 최대 500자까지 저장 가능
     */
    @Column(nullable = false, length = 500)
    private String title;  // 게시글 제목
    
    /**
     * columnDefinition = "TEXT"
     * - MySQL의 TEXT 타입으로 생성 (긴 텍스트 저장 가능)
     * - VARCHAR보다 큰 데이터 저장 가능
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 게시글 내용
    
    @Column(nullable = false, length = 50)
    private String author;  // 작성자
    
    /**
     * LocalDateTime: Java 8의 날짜/시간 타입
     * - 2024-01-01 12:30:45 같은 형식
     * - 데이터베이스의 DATETIME 타입과 매핑됨
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;  // 작성일시
    
    private LocalDateTime updatedAt;  // 수정일시 (nullable = true, 수정 안 할 수도 있음)
    
    /**
     * @PrePersist: 엔티티가 데이터베이스에 저장되기 전에 실행되는 메서드
     * - 새 게시글을 저장할 때 자동으로 실행됨
     * - createdAt과 updatedAt을 현재 시간으로 설정
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();  // 현재 시간으로 설정
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * @PreUpdate: 엔티티가 수정되기 전에 실행되는 메서드
     * - 게시글을 수정할 때 자동으로 실행됨
     * - updatedAt만 현재 시간으로 갱신
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();  // 수정 시간 갱신
    }
    
    /**
     * 게시글 수정 메서드
     * - 제목과 내용만 수정 가능
     * - updatedAt은 @PreUpdate가 자동으로 갱신
     * 
     * @param title 수정할 제목
     * @param content 수정할 내용
     */
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
        // updatedAt은 @PreUpdate가 자동으로 처리
    }
}
