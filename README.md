# 게시판 프로젝트

Spring Boot 백엔드 + React 프론트엔드로 구성된 모노레포 프로젝트입니다.

## 프로젝트 구조

```
java_board/
├── backend/          # Spring Boot 백엔드
│   ├── src/
│   └── pom.xml
├── frontend/         # React 프론트엔드 (Vite)
│   ├── src/
│   └── package.json
└── README.md
```

## 아키텍처

```
[React Frontend] → [Spring Boot Backend] → [MySQL/Elasticsearch]
```

- **Frontend**: React (Vite) - 사용자 인터페이스
- **Backend**: Spring Boot - 비즈니스 로직 및 데이터 처리

## 실행 방법

### 1. 백엔드 실행

```bash
cd backend
./start.sh
# 또는
mvn spring-boot:run
```

백엔드 서버: http://localhost:8081

### 2. 프론트엔드 실행

```bash
cd frontend
npm run dev
```

프론트엔드: http://localhost:5173

## API 엔드포인트

### 백엔드 API (Spring Boot)
- `GET /api/boards` - 게시글 목록 조회 (페이징 지원)
- `GET /api/boards/{id}` - 게시글 상세 조회
- `POST /api/boards` - 게시글 작성
- `PUT /api/boards/{id}` - 게시글 수정
- `DELETE /api/boards/{id}` - 게시글 삭제

**쿼리 파라미터:**
- `page`: 페이지 번호 (0부터 시작, 기본값: 0)
- `size`: 페이지 크기 (기본값: 10)
- `keyword`: 검색 키워드 (선택적)
- `searchType`: 검색 타입 (title, content, title+content, author, 기본값: title+content)

## 기술 스택

### Backend
- Spring Boot
- Spring Data JPA
- MySQL
- Elasticsearch
- Thymeleaf (기존 뷰, React로 대체 예정)

### Frontend
- React 19
- React Router
- Axios
- Vite

## 개발 환경

- Java 17+
- Node.js 18+
- MySQL 8.0+
- Elasticsearch 8.0+
