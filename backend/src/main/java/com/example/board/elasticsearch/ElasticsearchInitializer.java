package com.example.board.elasticsearch;

import com.example.board.entity.Board;
import com.example.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ElasticsearchInitializer: Elasticsearch 인덱스 초기화 및 데이터 동기화
 * 
 * 애플리케이션 시작 시 실행되는 컴포넌트
 * - application.yml의 board.elasticsearch.sync-on-startup 설정값이 true일 때만 실행
 * - 기존 인덱스를 삭제하고 새로 생성
 * - MySQL의 모든 게시글 데이터를 Elasticsearch에 동기화
 * 
 * @Component: Spring이 이 클래스를 빈으로 등록
 * @RequiredArgsConstructor: final 필드의 생성자 자동 생성
 * CommandLineRunner: 애플리케이션 시작 후 run() 메서드 실행
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ElasticsearchInitializer implements CommandLineRunner {
    
    private final ElasticsearchOperations elasticsearchOperations;
    private final BoardSearchRepository boardSearchRepository;
    private final BoardRepository boardRepository;
    
    /**
     * application.yml에서 설정값 주입
     * board.elasticsearch.sync-on-startup: true일 때만 초기화 실행
     */
    @Value("${board.elasticsearch.sync-on-startup:false}")
    private boolean syncOnStartup;
    
    /**
     * 애플리케이션 시작 시 실행되는 메서드
     * 
     * @param args 명령줄 인수 (사용하지 않음)
     */
    @Override
    public void run(String... args) {
        // 설정값이 false이면 초기화를 실행하지 않음
        if (!syncOnStartup) {
            log.info("board.elasticsearch.sync-on-startup이 false로 설정되어 있습니다. Elasticsearch 초기화를 건너뜁니다.");
            return;
        }
        
        log.warn("⚠️  board.elasticsearch.sync-on-startup이 true로 설정되어 있습니다.");
        log.warn("⚠️  기존 Elasticsearch 인덱스가 삭제되고 MySQL 데이터로 재동기화됩니다.");
        
        try {
            // 기존 인덱스 삭제
            deleteIndexIfExists();
            
            // 인덱스 생성
            createIndex();
            
            // MySQL 데이터 동기화
            syncAllBoards();
            
            log.info("✅ Elasticsearch 인덱스 초기화 및 데이터 동기화가 완료되었습니다.");
        } catch (Exception e) {
            log.error("❌ Elasticsearch 초기화 중 오류 발생", e);
        }
    }
    
    /**
     * 기존 인덱스가 존재하면 삭제
     */
    private void deleteIndexIfExists() {
        try {
            boolean indexExists = elasticsearchOperations.indexOps(BoardDocument.class).exists();
            
            if (indexExists) {
                log.info("기존 Elasticsearch 인덱스 'boards'를 삭제합니다.");
                elasticsearchOperations.indexOps(BoardDocument.class).delete();
                log.info("기존 Elasticsearch 인덱스 'boards' 삭제 완료");
            } else {
                log.info("삭제할 인덱스가 없습니다.");
            }
        } catch (Exception e) {
            log.error("인덱스 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 새로운 인덱스 생성
     */
    private void createIndex() {
        try {
            log.info("새로운 Elasticsearch 인덱스 'boards'를 생성합니다.");
            elasticsearchOperations.indexOps(BoardDocument.class).create();
            log.info("새로운 Elasticsearch 인덱스 'boards' 생성 완료");
        } catch (Exception e) {
            log.error("인덱스 생성 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * MySQL의 모든 게시글을 Elasticsearch에 동기화
     */
    private void syncAllBoards() {
        try {
            // MySQL에서 모든 게시글 조회
            List<Board> boards = boardRepository.findAll();
            
            if (boards.isEmpty()) {
                log.info("동기화할 게시글이 없습니다.");
                return;
            }
            
            log.info("MySQL에서 {}개의 게시글을 가져와 Elasticsearch에 동기화합니다.", boards.size());
            
            int successCount = 0;
            int failCount = 0;
            
            // 각 게시글을 BoardDocument로 변환하여 Elasticsearch에 저장
            for (Board board : boards) {
                try {
                    BoardDocument document = BoardDocument.builder()
                            .id(board.getId())
                            .title(board.getTitle())
                            .content(board.getContent())
                            .author(board.getAuthor())
                            .createdAt(board.getCreatedAt())
                            .updatedAt(board.getUpdatedAt())
                            .build();
                    
                    boardSearchRepository.save(document);
                    successCount++;
                } catch (Exception e) {
                    log.error("게시글 동기화 실패: boardId={}, error={}", 
                            board.getId(), e.getMessage(), e);
                    failCount++;
                }
            }
            
            log.info("Elasticsearch 초기 동기화 완료 - 성공: {}, 실패: {}", successCount, failCount);
        } catch (Exception e) {
            log.error("Elasticsearch 초기 동기화 중 오류 발생", e);
        }
    }
}
