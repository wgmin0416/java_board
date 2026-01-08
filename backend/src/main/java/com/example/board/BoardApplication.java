package com.example.board;

// Spring Boot 애플리케이션 실행 관련
import org.springframework.boot.SpringApplication;              // Spring Boot 애플리케이션 실행 클래스
import org.springframework.boot.autoconfigure.SpringBootApplication; // Spring Boot 자동 설정 어노테이션
// 스케줄링 기능 활성화
import org.springframework.scheduling.annotation.EnableScheduling;  // 스케줄링 기능 활성화 어노테이션

/**
 * BoardApplication: Spring Boot 애플리케이션의 시작점
 * 
 * 이 클래스는 Spring Boot 애플리케이션의 진입점(Entry Point)입니다.
 * Java 프로그램은 main 메서드부터 실행되며, 이 클래스의 main 메서드가
 * 전체 애플리케이션을 시작합니다.
 * 
 * @SpringBootApplication 어노테이션의 역할:
 * 이 어노테이션은 사실상 3개의 어노테이션을 합친 것입니다:
 * 
 * 1. @Configuration: 이 클래스가 설정 클래스임을 표시
 *    - Spring이 이 클래스를 설정 정보로 인식
 *    - 빈(Bean) 정의를 포함할 수 있음
 * 
 * 2. @EnableAutoConfiguration: Spring Boot의 자동 설정 활성화
 *    - 클래스패스에 있는 라이브러리를 보고 자동으로 설정
 *    - 예시:
 *      * spring-boot-starter-data-jpa가 있으면 → JPA 자동 설정
 *      * spring-boot-starter-web이 있으면 → 웹 서버(Tomcat) 자동 시작
 *      * MySQL 드라이버가 있으면 → 데이터베이스 연결 자동 설정
 *    - application.yml 파일의 설정을 읽어서 적용
 * 
 * 3. @ComponentScan: 현재 패키지와 하위 패키지에서 컴포넌트 스캔
 *    - @Controller, @Service, @Repository 등이 붙은 클래스를 찾음
 *    - 찾은 클래스들을 Spring 컨테이너에 빈으로 등록
 *    - 기본값: 현재 패키지(com.example.board)와 하위 패키지 모두 스캔
 * 
 * @EnableScheduling: 스케줄링 기능 활성화
 *    - @Scheduled 어노테이션이 붙은 메서드를 실행할 수 있게 함
 *    - 예: BoardSyncWorker의 syncBoards() 메서드가 30초마다 실행됨
 *    - 내부적으로 스레드 풀을 생성하여 스케줄링 작업을 처리
 */
@SpringBootApplication  // Spring Boot 자동 설정 + 컴포넌트 스캔 활성화
@EnableScheduling       // 스케줄링 기능 활성화 (30초마다 동기화 워커 실행)
public class BoardApplication {

    /**
     * main 메서드: 애플리케이션의 진입점
     * 
     * Java 프로그램은 항상 main 메서드부터 실행됩니다.
     * 이 메서드가 실행되면 Spring Boot 애플리케이션이 시작됩니다.
     * 
     * @param args 명령줄 인수 (프로그램 실행 시 전달되는 인수)
     *             예: java BoardApplication arg1 arg2
     *             이 프로젝트에서는 사용하지 않음
     * 
     * SpringApplication.run()의 동작 순서:
     * 
     * 1. 애플리케이션 컨텍스트(ApplicationContext) 생성
     *    - Spring 컨테이너를 생성하여 빈들을 관리할 공간을 만듦
     * 
     * 2. application.yml 파일 읽기
     *    - 데이터베이스 연결 정보, 서버 포트 등 설정 읽기
     *    - 읽은 설정을 Spring 환경에 적용
     * 
     * 3. 컴포넌트 스캔
     *    - com.example.board 패키지와 하위 패키지를 스캔
     *    - @Controller, @Service, @Repository 등이 붙은 클래스 찾기
     *    - 찾은 클래스들을 빈으로 등록
     * 
     * 4. 의존성 주입
     *    - 빈들 간의 의존관계를 분석
     *    - 생성자를 통해 의존성 주입
     *    - 예: BoardController에 BoardService 주입
     * 
     * 5. CommandLineRunner 실행
     *    - CommandLineRunner 인터페이스를 구현한 빈들의 run() 메서드 실행
     *    - 예: ElasticsearchInitializer.run() 실행
     * 
     * 6. 내장 웹 서버(Tomcat) 시작
     *    - application.yml의 server.port 설정에 따라 포트 결정 (기본값: 8080, 우리는 8081)
     *    - 웹 서버가 시작되면 HTTP 요청을 받을 수 있게 됨
     * 
     * 7. 애플리케이션 준비 완료
     *    - 모든 초기화 작업 완료
     *    - 웹 요청을 받을 준비가 됨
     *    - 서버가 종료될 때까지 계속 실행됨
     * 
     * 주의사항:
     * - 이 메서드는 서버가 종료될 때까지 계속 실행 중입니다
     * - return 문 이후의 코드는 서버가 종료되기 전까지 실행되지 않습니다
     */
    public static void main(String[] args) {
        // Spring Boot 애플리케이션 실행
        // BoardApplication.class: 이 클래스를 기준으로 설정을 읽음
        // args: 명령줄 인수 (사용하지 않음)
        // 
        // 이 한 줄로:
        // - 컴포넌트 스캔
        // - 의존성 주입
        // - 웹 서버 시작
        // - 모든 초기화 작업이 완료됨
        SpringApplication.run(BoardApplication.class, args);
        
        // ⚠️ 주의: 이 코드는 서버가 종료되기 전까지 실행되지 않습니다!
        // SpringApplication.run()은 웹 서버를 시작하고 계속 실행 중이므로
        // 이 코드에 도달하지 않습니다.
        // 서버를 종료하면(예: Ctrl+C) 이제서야 여기에 도달할 수 있습니다.
    }
}

