package com.example.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * BoardApplication: Spring Boot 애플리케이션의 시작점
 * 
 * @SpringBootApplication 어노테이션의 역할:
 * 1. @Configuration: 이 클래스가 설정 클래스임을 표시
 * 2. @EnableAutoConfiguration: Spring Boot의 자동 설정 활성화
 *    - 데이터베이스 연결 자동 설정
 *    - 내장 서버(Tomcat) 자동 시작
 *    - 필요한 빈(Bean) 객체 자동 생성
 * 3. @ComponentScan: 현재 패키지와 하위 패키지에서 컴포넌트 스캔
 *    - @Controller, @Service, @Repository 등을 찾아서 등록
 * 
 * main 메서드:
 * - SpringApplication.run()이 Spring Boot 애플리케이션을 시작합니다
 * - 이 메서드가 실행되면:
 *   1. application.yml 파일을 읽어서 설정 적용
 *   2. 내장 Tomcat 서버가 시작됨 (기본 포트: 8080, 우리는 8081로 설정)
 *   3. 모든 컴포넌트를 스캔하고 초기화
 *   4. 웹 애플리케이션이 준비되어 요청을 받을 수 있게 됨
 */
@SpringBootApplication
public class BoardApplication {

    /**
     * main 메서드: 애플리케이션의 진입점
     * 
     * @param args 명령줄 인수 (사용하지 않음)
     * 
     * SpringApplication.run()의 동작:
     * - BoardApplication.class를 기준으로 모든 설정을 읽음
     * - com.example.board 패키지와 하위 패키지를 스캔
     * - @Controller, @Service 등이 붙은 클래스를 찾아서 객체 생성
     * - 웹 서버를 시작하고 대기 상태로 전환
     */
    public static void main(String[] args) {
        // Spring Boot 애플리케이션 실행
        // 이 한 줄로 모든 것이 시작됩니다!
        SpringApplication.run(BoardApplication.class, args);
        
        // 이 코드 이후에는 서버가 계속 실행 중이므로
        // 여기 도달하지 않습니다 (서버가 종료되기 전까지)
    }
}

