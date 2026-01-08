import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import BoardList from "./components/BoardList";
import BoardDetail from "./components/BoardDetail";
import BoardForm from "./components/BoardForm";
import "./style.css";

/**
 * App: 메인 애플리케이션 컴포넌트
 *
 * React Router를 사용한 라우팅 설정
 */
function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 루트 경로는 게시판 목록으로 리다이렉트 */}
        <Route path="/" element={<Navigate to="/boards" replace />} />

        {/* 게시판 목록 */}
        <Route path="/boards" element={<BoardList />} />

        {/* 게시글 상세 */}
        <Route path="/boards/:id" element={<BoardDetail />} />

        {/* 게시글 작성 */}
        <Route path="/boards/new" element={<BoardForm />} />

        {/* 게시글 수정 */}
        <Route path="/boards/:id/edit" element={<BoardForm />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
