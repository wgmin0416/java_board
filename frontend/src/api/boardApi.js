/**
 * Board API 클라이언트
 *
 * Spring Boot 백엔드 API를 직접 호출하는 함수들
 */

import axios from "axios";

const API_BASE_URL = "http://localhost:8081/api"; // Spring Boot 백엔드 URL

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

/**
 * 게시글 목록 조회 (페이징)
 *
 * @param {Object} params - 쿼리 파라미터
 * @param {number} params.page - 페이지 번호 (0부터 시작)
 * @param {number} params.size - 페이지 크기
 * @param {string} params.sort - 정렬 (desc: 최신순, asc: 오래된순)
 * @param {string} params.keyword - 검색 키워드 (선택적)
 * @param {string} params.searchType - 검색 타입 (선택적)
 * @returns {Promise} 페이징된 게시글 목록
 */
export const getBoards = async (params = {}) => {
  const {
    page = 0,
    size = 10,
    sort = "desc",
    keyword,
    searchType = "title+content",
  } = params;

  const queryParams = new URLSearchParams({
    page: page.toString(),
    size: size.toString(),
    sort: `createdAt,${sort}`, // Spring Data 형식: sort=createdAt,desc
  });

  // 검색어가 있을 때만 keyword와 searchType을 쿼리스트링에 추가
  if (keyword) {
    queryParams.append("keyword", keyword);
    queryParams.append("searchType", searchType);
  }

  const response = await api.get(`/boards?${queryParams}`);
  return response.data;
};

/**
 * 게시글 상세 조회
 *
 * @param {number} id - 게시글 번호
 * @returns {Promise} 게시글 정보
 */
export const getBoard = async (id) => {
  const response = await api.get(`/boards/${id}`);
  return response.data;
};

/**
 * 게시글 작성
 *
 * @param {Object} data - 게시글 데이터
 * @param {string} data.title - 제목
 * @param {string} data.content - 내용
 * @param {string} data.author - 작성자
 * @returns {Promise} 작성된 게시글 정보
 */
export const createBoard = async (data) => {
  const response = await api.post("/boards", data);
  return response.data;
};

/**
 * 게시글 수정
 *
 * @param {number} id - 게시글 번호
 * @param {Object} data - 수정할 데이터
 * @param {string} data.title - 제목
 * @param {string} data.content - 내용
 * @returns {Promise} 수정된 게시글 정보
 */
export const updateBoard = async (id, data) => {
  const response = await api.put(`/boards/${id}`, data);
  return response.data;
};

/**
 * 게시글 삭제
 *
 * @param {number} id - 게시글 번호
 * @returns {Promise}
 */
export const deleteBoard = async (id) => {
  await api.delete(`/boards/${id}`);
};
