package com.example.board.repository;

// 엔티티 클래스
import com.example.board.entity.Board;
// Spring Data JPA: JPA를 더 쉽게 사용할 수 있게 해주는 프레임워크
import org.springframework.data.jpa.repository.JpaRepository;  // 기본 CRUD 메서드를 제공하는 인터페이스
// Spring 어노테이션
import org.springframework.stereotype.Repository;  // Repository 계층 표시 어노테이션

/**
 * BoardRepository: 게시판 데이터 접근 계층
 * 
 * Repository란?
 * - 데이터베이스와 직접 소통하는 계층
 * - CRUD (Create, Read, Update, Delete) 작업을 수행
 * - Service 계층에서 이 Repository를 사용해서 데이터를 가져오거나 저장
 * - 데이터베이스 쿼리를 실행하는 역할
 * 
 * 인터페이스인 이유:
 * - Spring Data JPA가 자동으로 구현체를 생성해줌
 * - 우리는 메서드 시그니처만 선언하면 됨
 * - 실제 SQL 쿼리는 Spring Data JPA가 자동 생성
 * 
 * @Repository: 이 인터페이스가 Repository 계층임을 표시하는 어노테이션
 *              - Spring이 이 인터페이스를 인식하고 자동으로 구현체를 생성
 *              - 빈으로 등록되어 다른 클래스에서 주입받아 사용 가능
 *              - 예: BoardService에서 @RequiredArgsConstructor로 주입받음
 * 
 * JpaRepository<Board, Long>:
 * - Spring Data JPA가 제공하는 인터페이스
 * - 이걸 상속받으면 기본적인 CRUD 메서드가 자동으로 제공됨
 * - 제네릭 타입:
 *   * Board: 엔티티 타입 (어떤 테이블을 다룰지)
 *   * Long: 기본키(Primary Key)의 타입 (Board의 id 필드 타입)
 * 
 * 자동으로 제공되는 메서드들:
 * 
 * 1. save(Board board)
 *    - 새로운 데이터면: INSERT INTO boards (title, content, ...) VALUES (...)
 *    - 기존 데이터면: UPDATE boards SET title=?, content=? WHERE id=?
 *    - 반환: 저장된 Board 객체
 * 
 * 2. findById(Long id)
 *    - SQL: SELECT * FROM boards WHERE id = ?
 *    - 반환: Optional<Board> (데이터가 없을 수도 있음을 표현)
 *    - 사용 예: boardRepository.findById(1L).orElseThrow(...)
 * 
 * 3. findAll()
 *    - SQL: SELECT * FROM boards
 *    - 반환: List<Board>
 * 
 * 4. findAll(Pageable pageable)
 *    - SQL: SELECT * FROM boards LIMIT ? OFFSET ?
 *    - 반환: Page<Board> (페이징된 결과)
 * 
 * 5. deleteById(Long id)
 *    - SQL: DELETE FROM boards WHERE id = ?
 *    - 반환: void
 * 
 * 6. existsById(Long id)
 *    - SQL: SELECT COUNT(*) > 0 FROM boards WHERE id = ?
 *    - 반환: boolean (존재하면 true, 없으면 false)
 * 
 * 7. count()
 *    - SQL: SELECT COUNT(*) FROM boards
 *    - 반환: long (전체 개수)
 * 
 * 커스텀 메서드 추가 가능:
 * - 메서드 이름을 분석해서 자동으로 SQL 생성
 * - 예: List<Board> findByTitleContaining(String title)
 *   → SELECT * FROM boards WHERE title LIKE '%?%'
 * - 규칙:
 *   * findBy: 조회 시작
 *   * Title: 필드 이름 (대소문자 구분)
 *   * Containing: LIKE '%?%' 검색
 * 
 * 이 인터페이스만 선언하면 Spring이 자동으로 구현체를 만들어줍니다!
 * 우리는 구현 코드를 작성할 필요가 없습니다.
 * 
 * 동작 원리:
 * 1. 애플리케이션 시작 시 Spring이 이 인터페이스를 스캔
 * 2. JpaRepository를 상속받은 것을 확인
 * 3. 프록시 객체를 생성하여 구현체를 만듦
 * 4. 메서드 호출 시 적절한 SQL 쿼리를 생성하고 실행
 */
@Repository  // Repository 계층 표시
public interface BoardRepository extends JpaRepository<Board, Long> {
    // JpaRepository<엔티티타입, 기본키타입>
    // Board: 엔티티 타입 (boards 테이블)
    // Long: 기본키 타입 (id 필드의 타입)
    
    // 필요하면 여기에 커스텀 메서드를 추가할 수 있습니다
    // 예: 제목으로 검색
    // List<Board> findByTitleContaining(String title);
    // 
    // Spring Data JPA가 메서드 이름을 분석해서 자동으로 SQL을 만들어줍니다!
    // findByTitleContaining → SELECT * FROM boards WHERE title LIKE '%?%'
    // 
    // 메서드 이름 규칙:
    // - findBy + 필드명 + 조건
    // - 예: findByTitleContaining → title 필드에서 포함 검색
    // - 예: findByAuthor → author 필드에서 정확한 일치 검색
    // - 예: findByCreatedAtBetween → createdAt 필드에서 범위 검색
}

