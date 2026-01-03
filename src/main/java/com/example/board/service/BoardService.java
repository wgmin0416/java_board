package com.example.board.service;

import com.example.board.dto.BoardRequest;
import com.example.board.dto.BoardResponse;
import com.example.board.entity.Board;
import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * BoardService: 게시판 비즈니스 로직 처리 계층
 * 
 * Service 계층의 역할:
 * 1. 비즈니스 로직 처리 (게시글 작성 규칙, 권한 확인 등)
 * 2. 트랜잭션 관리 (여러 작업을 하나의 단위로 묶어서 처리)
 * 3. Entity ↔ DTO 변환
 * 4. 예외 처리
 * 
 * @Service: 이 클래스가 Service 계층임을 표시
 *           Spring이 이 클래스를 빈(Bean)으로 등록하고 관리
 * 
 * @RequiredArgsConstructor: Lombok이 final 필드의 생성자를 자동 생성
 *                          의존성 주입(Dependency Injection)에 사용
 *                          예: private final BoardRepository boardRepository;
 *                              → 생성자에서 자동으로 주입됨
 */
@Service
@RequiredArgsConstructor  // final 필드의 생성자 자동 생성 (의존성 주입용)
@Transactional             // 이 클래스의 모든 메서드가 트랜잭션 안에서 실행됨
public class BoardService {
    
    /**
     * BoardRepository: 데이터 접근 계층
     * 
     * final 키워드:
     * - 한 번 할당되면 변경 불가
     * - 생성자에서만 초기화 가능
     * - @RequiredArgsConstructor가 이 필드를 위한 생성자 파라미터를 자동 생성
     * 
     * 의존성 주입(Dependency Injection):
     * - Spring이 자동으로 BoardRepository 구현체를 주입해줌
     * - 우리는 new BoardRepository() 같은 코드를 작성할 필요 없음
     * - Spring이 알아서 관리해줌
     */
    private final BoardRepository boardRepository;
    
    /**
     * 게시글 작성
     * 
     * @Transactional: 이 메서드가 트랜잭션 안에서 실행됨
     *                 - 성공하면 커밋 (데이터베이스에 저장)
     *                 - 실패하면 롤백 (변경사항 취소)
     * 
     * @param request 게시글 작성 요청 데이터 (제목, 내용, 작성자)
     * @return 작성된 게시글 정보
     */
    public BoardResponse createBoard(BoardRequest request) {
        // 1. DTO를 Entity로 변환
        // Builder 패턴으로 Board 객체 생성
        Board board = Board.builder()
                .title(request.getTitle())      // 요청에서 제목 가져오기
                .content(request.getContent())  // 요청에서 내용 가져오기
                .author(request.getAuthor())   // 요청에서 작성자 가져오기
                .build();                       // Board 객체 생성
        
        // 2. 데이터베이스에 저장
        // save() 메서드가 INSERT 쿼리를 실행
        // @PrePersist가 실행되어 createdAt, updatedAt이 자동 설정됨
        Board savedBoard = boardRepository.save(board);
        
        // 3. Entity를 DTO로 변환해서 반환
        // Controller는 Entity를 직접 받지 않고 DTO를 받음
        return BoardResponse.from(savedBoard);
    }
    
    /**
     * 게시글 조회 (단건)
     * 
     * @Transactional(readOnly = true): 읽기 전용 트랜잭션
     *                                 - 성능 최적화 (쓰기 작업이 없으므로)
     *                                 - 데이터 변경 불가
     * 
     * @param id 조회할 게시글 번호
     * @return 게시글 정보
     * @throws ResponseStatusException 게시글을 찾을 수 없을 때 (404 NOT_FOUND)
     */
    @Transactional(readOnly = true)  // 읽기 전용 (성능 최적화)
    public BoardResponse getBoard(Long id) {
        // findById()는 Optional<Board>를 반환
        // Optional: 값이 있을 수도 있고 없을 수도 있음을 표현
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "게시글을 찾을 수 없습니다: " + id));
        // orElseThrow(): 값이 없으면 404 NOT_FOUND 예외를 던짐
        // 값이 있으면 Board 객체 반환
        // ResponseStatusException: Spring이 HTTP 상태 코드를 자동으로 처리해줌
        
        return BoardResponse.from(board);
    }
    
    /**
     * 게시글 목록 조회 (페이징)
     * 
     * 페이징이란?
     * - 모든 데이터를 한 번에 가져오지 않고 페이지 단위로 나눠서 가져오기
     * - 예: 1페이지에 10개씩 보여주기
     * 
     * Pageable: 페이징 정보를 담는 객체
     * - page: 현재 페이지 번호 (0부터 시작)
     * - size: 한 페이지에 보여줄 개수
     * 
     * @param pageable 페이징 정보 (페이지 번호, 크기 등)
     * @return 페이징된 게시글 목록
     */
    @Transactional(readOnly = true)
    public Page<BoardResponse> getAllBoards(Pageable pageable) {
        // findAll(pageable): 페이징 정보를 포함해서 조회
        // Page<Board>: 페이징된 결과를 담는 객체
        //   - content: 실제 데이터 리스트
        //   - totalElements: 전체 개수
        //   - totalPages: 전체 페이지 수
        //   - number: 현재 페이지 번호
        return boardRepository.findAll(pageable)
                .map(BoardResponse::from);
        // map(): 각 Board를 BoardResponse로 변환
        // BoardResponse::from: 메서드 레퍼런스 (람다식의 축약형)
        //   board -> BoardResponse.from(board) 와 동일
    }
    
    /**
     * 게시글 수정
     * 
     * @param id 수정할 게시글 번호
     * @param request 수정할 내용 (제목, 내용)
     * @return 수정된 게시글 정보
     * @throws ResponseStatusException 게시글을 찾을 수 없을 때 (404 NOT_FOUND)
     */
    public BoardResponse updateBoard(Long id, BoardRequest request) {
        // 1. 기존 게시글 조회
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, 
                        "게시글을 찾을 수 없습니다: " + id));
        
        // 2. 게시글 수정
        // Entity의 update() 메서드 호출
        // @PreUpdate가 실행되어 updatedAt이 자동 갱신됨
        board.update(request.getTitle(), request.getContent());
        
        // 3. 수정된 내용을 데이터베이스에 저장
        // save()는 새로운 데이터면 INSERT, 기존 데이터면 UPDATE
        Board updatedBoard = boardRepository.save(board);
        
        // 4. DTO로 변환해서 반환
        return BoardResponse.from(updatedBoard);
    }
    
    /**
     * 게시글 삭제
     * 
     * @param id 삭제할 게시글 번호
     * @throws ResponseStatusException 게시글을 찾을 수 없을 때 (404 NOT_FOUND)
     */
    public void deleteBoard(Long id) {
        // 게시글이 존재하는지 먼저 확인
        if (!boardRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, 
                    "게시글을 찾을 수 없습니다: " + id);
        }
        
        // deleteById(): 데이터베이스에서 삭제
        // DELETE FROM boards WHERE id = ?
        boardRepository.deleteById(id);
    }
}

