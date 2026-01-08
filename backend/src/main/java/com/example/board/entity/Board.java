package com.example.board.entity;

// JPA (Java Persistence API): 데이터베이스와 Java 객체를 매핑하는 API
import jakarta.persistence.*;  // JPA 어노테이션들 (@Entity, @Id, @Column 등)
// Lombok: 코드 자동 생성 라이브러리
import lombok.AllArgsConstructor;  // 모든 필드를 파라미터로 받는 생성자 자동 생성
import lombok.Builder;            // 빌더 패턴 자동 생성
import lombok.Getter;             // 모든 필드의 getter 메서드 자동 생성
import lombok.NoArgsConstructor;  // 기본 생성자 자동 생성 (JPA에서 필요)

// Java 8의 날짜/시간 API
import java.time.LocalDateTime;   // 날짜+시간을 표현하는 클래스 (예: 2024-01-01T12:30:45)

/**
 * Board: 게시판 엔티티 클래스
 * 
 * 엔티티(Entity)란?
 * - 데이터베이스의 테이블과 Java 클래스를 연결하는 역할
 * - 이 클래스를 보고 JPA가 자동으로 데이터베이스 테이블을 생성하거나 매핑합니다
 * - 예: Board 클래스 → boards 테이블
 * 
 * 엔티티의 역할:
 * 1. 데이터베이스 테이블 구조 정의
 * 2. Java 객체 ↔ 데이터베이스 레코드 변환
 * 3. JPA가 이 클래스를 보고 SQL 쿼리를 자동 생성
 * 
 * @Entity: 이 클래스가 JPA 엔티티임을 표시하는 어노테이션
 *          - JPA가 이 클래스를 인식하고 데이터베이스와 연결
 *          - application.yml의 jpa.hibernate.ddl-auto 설정에 따라
 *            테이블을 자동 생성하거나 매핑
 *          - 예: ddl-auto: update → 테이블이 없으면 생성, 있으면 매핑만 수행
 * 
 * @Table: 데이터베이스 테이블 이름 지정
 *         - name = "boards": 데이터베이스에 "boards" 테이블로 생성됨
 *         - 생략하면 클래스 이름을 소문자로 변환한 이름 사용 (Board → board)
 * 
 * Lombok 어노테이션 설명:
 * 
 * @Getter: 모든 필드의 getter 메서드를 자동 생성
 *          - 예: private String title; → public String getTitle() { return title; }
 *          - 필드에 직접 접근하지 않고 getter를 통해 접근 (캡슐화)
 * 
 * @NoArgsConstructor: 기본 생성자 자동 생성 (파라미터 없음)
 *                     - JPA에서 엔티티를 조회할 때 필요
 *                     - 예: public Board() { }
 * 
 * @AllArgsConstructor: 모든 필드를 파라미터로 받는 생성자 자동 생성
 *                      - 예: public Board(Long id, String title, ...) { this.id = id; ... }
 * 
 * @Builder: 빌더 패턴 자동 생성
 *           - 객체 생성 시 편리하게 사용 가능
 *           - 예: Board.builder().title("제목").content("내용").build()
 *           - 필드를 하나씩 설정하면서 객체를 만들 수 있음
 */
@Entity                    // JPA 엔티티 표시
@Table(name = "boards")   // 데이터베이스 테이블 이름: boards
@Getter                    // 모든 필드의 getter 메서드 자동 생성
@NoArgsConstructor        // 기본 생성자 자동 생성 (JPA에서 필요)
@AllArgsConstructor       // 모든 필드를 파라미터로 받는 생성자 자동 생성
@Builder                  // 빌더 패턴 자동 생성 (객체 생성 시 편리함)
public class Board {
    
    /**
     * 게시글 번호 (기본키)
     * 
     * @Id: 이 필드가 기본키(Primary Key)임을 표시하는 어노테이션
     *      - 기본키: 테이블에서 각 레코드를 고유하게 식별하는 필드
     *      - 데이터베이스에서 이 필드로 레코드를 찾을 수 있음
     *      - 예: SELECT * FROM boards WHERE id = 1
     * 
     * @GeneratedValue: 기본키 자동 생성 전략을 지정하는 어노테이션
     *   strategy = GenerationType.IDENTITY
     *   - IDENTITY: 데이터베이스의 자동 증가 기능 사용
     *   - MySQL의 AUTO_INCREMENT와 같은 역할
     *   - 데이터베이스가 자동으로 번호를 증가시켜서 할당
     *   - 예: 첫 번째 게시글 → id = 1, 두 번째 → id = 2, ...
     *   - 우리가 직접 번호를 지정할 필요 없음
     * 
     * Long 타입:
     * - 64비트 정수 타입
     * - -9,223,372,036,854,775,808 ~ 9,223,372,036,854,775,807 범위
     * - 게시글 번호로는 충분히 큰 범위
     * 
     * private: 외부에서 직접 접근 불가 (캡슐화)
     *          - getter를 통해서만 접근 가능 (@Getter가 자동 생성)
     */
    @Id                                                    // 기본키 표시
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // 자동 증가 (MySQL AUTO_INCREMENT)
    private Long id;  // 게시글 번호 (자동 증가, 예: 1, 2, 3, ...)
    
    /**
     * 게시글 제목
     * 
     * @Column: 데이터베이스 컬럼 설정을 지정하는 어노테이션
     *   nullable = false: NULL 값 허용 안 함 (필수 입력)
     *                    - 데이터베이스에 NOT NULL 제약조건 추가
     *                    - 이 필드가 없으면 게시글을 저장할 수 없음
     *   length = 500: 최대 500자까지 저장 가능
     *                - VARCHAR(500) 타입으로 생성됨
     *                - 500자를 초과하면 저장 시 오류 발생
     * 
     * String 타입:
     * - Java의 문자열 타입
     * - 데이터베이스에서는 VARCHAR 또는 TEXT 타입으로 매핑됨
     */
    @Column(nullable = false, length = 500)  // NOT NULL, 최대 500자
    private String title;  // 게시글 제목
    
    /**
     * 게시글 내용
     * 
     * columnDefinition = "TEXT"
     * - MySQL의 TEXT 타입으로 생성
     * - VARCHAR보다 큰 데이터 저장 가능
     * - TEXT 타입: 최대 65,535자까지 저장 가능
     * - 제목은 짧지만 내용은 길 수 있으므로 TEXT 타입 사용
     * 
     * nullable = false: 필수 입력
     */
    @Column(nullable = false, columnDefinition = "TEXT")  // NOT NULL, TEXT 타입
    private String content;  // 게시글 내용
    
    /**
     * 작성자
     * 
     * @Column(nullable = false, length = 50)
     * - nullable = false: 필수 입력
     * - length = 50: 최대 50자까지 저장 가능
     *                - 작성자 이름은 보통 짧으므로 VARCHAR(50) 사용
     */
    @Column(nullable = false, length = 50)  // NOT NULL, 최대 50자
    private String author;  // 작성자
    
    /**
     * 작성일시
     * 
     * LocalDateTime: Java 8의 날짜/시간 타입
     * - 날짜와 시간을 함께 표현하는 클래스
     * - 형식: 2024-01-01T12:30:45 (ISO 8601 형식)
     * - 데이터베이스의 DATETIME 타입과 매핑됨
     * - 예: 2024-01-01 12:30:45
     * 
     * nullable = false: 필수 입력
     *                  - 게시글을 작성할 때 반드시 시간이 설정되어야 함
     *                  - @PrePersist가 자동으로 현재 시간 설정
     */
    @Column(nullable = false)  // NOT NULL
    private LocalDateTime createdAt;  // 작성일시 (예: 2024-01-01T12:30:45)
    
    /**
     * 수정일시
     * 
     * nullable = true (기본값)
     * - 수정하지 않은 게시글은 updatedAt이 null일 수 있음
     * - 하지만 @PrePersist에서 초기값을 설정하므로 실제로는 null이 아님
     * 
     * @PreUpdate가 실행되면 자동으로 현재 시간으로 갱신됨
     */
    private LocalDateTime updatedAt;  // 수정일시 (nullable = true, 수정 안 할 수도 있음)
    
    /**
     * @PrePersist: 엔티티가 데이터베이스에 저장되기 전에 실행되는 메서드
     * 
     * 실행 시점:
     * - 새 게시글을 저장할 때 (INSERT 쿼리 실행 전)
     * - boardRepository.save(board) 호출 시 자동 실행
     * 
     * 역할:
     * - createdAt과 updatedAt을 현재 시간으로 설정
     * - 우리가 직접 시간을 설정하지 않아도 자동으로 처리됨
     * 
     * protected: 같은 패키지 또는 하위 클래스에서만 접근 가능
     *            - 외부에서 직접 호출하지 않고 JPA가 자동으로 호출
     * 
     * LocalDateTime.now(): 현재 날짜/시간을 반환
     *                     - 예: 2024-01-01T12:30:45.123456789
     */
    @PrePersist  // 저장 전 실행
    protected void onCreate() {
        createdAt = LocalDateTime.now();  // 작성일시를 현재 시간으로 설정
        updatedAt = LocalDateTime.now();  // 수정일시도 현재 시간으로 설정 (처음에는 작성일시와 동일)
    }
    
    /**
     * @PreUpdate: 엔티티가 수정되기 전에 실행되는 메서드
     * 
     * 실행 시점:
     * - 기존 게시글을 수정할 때 (UPDATE 쿼리 실행 전)
     * - boardRepository.save(board) 호출 시 (기존 데이터인 경우)
     * 
     * 역할:
     * - updatedAt만 현재 시간으로 갱신
     * - createdAt은 변경하지 않음 (작성일시는 변경되면 안 됨)
     * 
     * 주의:
     * - 새로 생성할 때는 @PrePersist만 실행됨
     * - 수정할 때는 @PrePersist는 실행되지 않고 @PreUpdate만 실행됨
     */
    @PreUpdate  // 수정 전 실행
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();  // 수정일시를 현재 시간으로 갱신
        // createdAt은 변경하지 않음 (작성일시는 유지)
    }
    
    /**
     * 게시글 수정 메서드
     * 
     * 역할:
     * - 제목과 내용만 수정 가능
     * - 작성자(author)는 수정 불가 (보안상의 이유)
     * - 작성일시(createdAt)는 변경 불가 (데이터 무결성)
     * 
     * updatedAt 자동 갱신:
     * - 이 메서드를 호출한 후 boardRepository.save()를 호출하면
     * - @PreUpdate가 자동으로 실행되어 updatedAt이 현재 시간으로 갱신됨
     * - 우리가 직접 updatedAt을 설정할 필요 없음
     * 
     * @param title 수정할 제목
     * @param content 수정할 내용
     * 
     * 사용 예시:
     * Board board = boardRepository.findById(1L).get();
     * board.update("새 제목", "새 내용");
     * boardRepository.save(board);  // @PreUpdate가 실행되어 updatedAt 갱신
     */
    public void update(String title, String content) {
        this.title = title;        // 제목 수정
        this.content = content;   // 내용 수정
        // updatedAt은 @PreUpdate가 자동으로 처리
        // 작성자(author)와 작성일시(createdAt)는 변경하지 않음
    }
}
