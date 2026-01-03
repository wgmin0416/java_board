package com.example.board.repository;

import com.example.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * BoardRepository: 게시판 데이터 접근 계층
 * 
 * Repository란?
 * - 데이터베이스와 직접 소통하는 계층
 * - CRUD (Create, Read, Update, Delete) 작업을 수행
 * - Service 계층에서 이 Repository를 사용해서 데이터를 가져오거나 저장
 * 
 * @Repository: 이 인터페이스가 Repository 계층임을 표시
 *              Spring이 이 인터페이스를 인식하고 자동으로 구현체를 생성
 * 
 * JpaRepository<Board, Long>:
 * - Spring Data JPA가 제공하는 인터페이스
 * - 이걸 상속받으면 기본적인 CRUD 메서드가 자동으로 제공됨
 * 
 * 제네릭 타입 설명:
 * - Board: 엔티티 타입 (어떤 테이블을 다룰지)
 * - Long: 기본키(Primary Key)의 타입 (Board의 id 필드 타입)
 * 
 * 자동으로 제공되는 메서드들:
 * 1. save(Board board) 
 *    → INSERT INTO boards ... (새로운 데이터)
 *    → UPDATE boards SET ... (기존 데이터 수정)
 * 
 * 2. findById(Long id)
 *    → SELECT * FROM boards WHERE id = ?
 *    → Optional<Board> 반환 (데이터가 없을 수도 있음)
 * 
 * 3. findAll()
 *    → SELECT * FROM boards
 *    → List<Board> 반환
 * 
 * 4. deleteById(Long id)
 *    → DELETE FROM boards WHERE id = ?
 * 
 * 5. count()
 *    → SELECT COUNT(*) FROM boards
 *    → long 반환
 * 
 * 이 인터페이스만 선언하면 Spring이 자동으로 구현체를 만들어줍니다!
 * 우리는 구현 코드를 작성할 필요가 없습니다.
 */
@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    
    // 필요하면 여기에 커스텀 메서드를 추가할 수 있습니다
    // 예: 제목으로 검색
    // List<Board> findByTitleContaining(String title);
    // 
    // Spring Data JPA가 메서드 이름을 분석해서 자동으로 SQL을 만들어줍니다!
    // findByTitleContaining → SELECT * FROM boards WHERE title LIKE '%?%'
}

