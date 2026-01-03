#!/bin/bash

# Spring Boot 게시판 서버 실행 스크립트

echo "=== Spring Boot 게시판 서버 시작 ==="
echo ""

# Java 17 경로 설정
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"

# 현재 디렉토리로 이동
cd "$(dirname "$0")"

echo "Java 버전 확인:"
java -version
echo ""

echo "Maven 버전 확인:"
mvn -version | head -1
echo ""

echo "애플리케이션을 시작합니다..."
echo "서버가 시작되면 http://localhost:8081/boards 에서 접근할 수 있습니다."
echo "종료하려면 Ctrl+C를 누르세요."
echo ""

# Maven으로 Spring Boot 실행
mvn spring-boot:run

