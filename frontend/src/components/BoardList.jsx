import { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { getBoards, deleteBoard } from '../api/boardApi';
import '../style.css';

/**
 * BoardList: 게시글 목록 컴포넌트
 * 
 * 기능:
 * - 게시글 목록 표시 (페이징)
 * - 검색 기능
 * - 게시글 삭제
 * - 페이지 이동
 */
function BoardList() {
    const [boards, setBoards] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [keyword, setKeyword] = useState('');
    const [searchType, setSearchType] = useState('title+content');
    const [sort, setSort] = useState('desc'); // 최신순(desc), 오래된순(asc)
    const [size, setSize] = useState(5); // 5, 10, 20
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    // 게시글 목록 조회
    const fetchBoards = async (page = currentPage, sortValue = sort, sizeValue = size) => {
        setLoading(true);
        try {
            const params = {
                page,
                size: sizeValue,
                sort: sortValue,
            };
            
            if (keyword.trim()) {
                params.keyword = keyword.trim();
                params.searchType = searchType;
            }
            
            const data = await getBoards(params);
            setBoards(data.content || []);
            setCurrentPage(data.number || 0);
            setTotalPages(data.totalPages || 0);
            setTotalElements(data.totalElements || 0);
        } catch (error) {
            console.error('게시글 목록 조회 실패:', error);
            alert('게시글 목록을 불러오는데 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    // 컴포넌트 마운트 시 및 페이지 변경 시 데이터 조회
    useEffect(() => {
        fetchBoards(currentPage);
    }, [currentPage]);

    // 검색 실행
    const handleSearch = (e) => {
        e.preventDefault();
        setCurrentPage(0); // 검색 시 첫 페이지로 이동
        fetchBoards(0);
    };

    // 정렬 변경
    const handleSortChange = (e) => {
        setSort(e.target.value);
        setCurrentPage(0); // 정렬 변경 시 첫 페이지로 이동
        // sort state가 업데이트되기 전이므로 직접 값을 전달
        fetchBoards(0, e.target.value, size);
    };

    // 개수 변경
    const handleSizeChange = (e) => {
        const newSize = Number(e.target.value);
        setSize(newSize);
        setCurrentPage(0); // 개수 변경 시 첫 페이지로 이동
        // size state가 업데이트되기 전이므로 직접 값을 전달
        fetchBoards(0, sort, newSize);
    };

    // 게시글 삭제
    const handleDelete = async (id) => {
        if (!window.confirm('정말 삭제하시겠습니까?')) {
            return;
        }

        try {
            await deleteBoard(id);
            alert('게시글이 삭제되었습니다.');
            fetchBoards(currentPage); // 목록 새로고침
        } catch (error) {
            console.error('게시글 삭제 실패:', error);
            alert('게시글 삭제에 실패했습니다.');
        }
    };

    // 날짜 포맷팅
    const formatDate = (dateString) => {
        if (!dateString) return '';
        const date = new Date(dateString);
        return date.toLocaleString('ko-KR', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
        });
    };

    // 페이지 번호 배열 생성
    const getPageNumbers = () => {
        const pages = [];
        const startPage = Math.max(0, currentPage - 4);
        const endPage = Math.min(totalPages - 1, currentPage + 5);
        
        for (let i = startPage; i <= endPage; i++) {
            pages.push(i);
        }
        return pages;
    };

    return (
        <div className="container">
            <header>
                <h1>게시판</h1>
                <div className="header-actions">
                    <Link to="/boards/new" className="btn btn-primary">
                        게시글 작성
                    </Link>
                </div>
            </header>

            {/* 검색 폼 */}
            <form className="search-form" onSubmit={handleSearch}>
                <div className="search-container">
                    <div className="search-input-group">
                        <select
                            className="search-type-select"
                            value={searchType}
                            onChange={(e) => setSearchType(e.target.value)}
                        >
                            <option value="title">제목</option>
                            <option value="content">내용</option>
                            <option value="title+content">제목+내용</option>
                            <option value="author">작성자</option>
                        </select>
                        <input
                            type="text"
                            className="search-input"
                            placeholder="검색어를 입력하세요"
                            value={keyword}
                            onChange={(e) => setKeyword(e.target.value)}
                        />
                        <button type="submit" className="btn btn-search">
                            검색
                        </button>
                    </div>
                </div>
            </form>

            {/* 정렬 및 개수 설정 */}
            <div style={{ display: 'flex', gap: '10px', marginBottom: '20px', alignItems: 'center' }}>
                <label style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <span>정렬:</span>
                    <select
                        value={sort}
                        onChange={handleSortChange}
                        style={{ padding: '5px 10px', borderRadius: '4px', border: '1px solid #ddd' }}
                    >
                        <option value="desc">최신순</option>
                        <option value="asc">오래된순</option>
                    </select>
                </label>
                <label style={{ display: 'flex', alignItems: 'center', gap: '5px' }}>
                    <span>개수:</span>
                    <select
                        value={size}
                        onChange={handleSizeChange}
                        style={{ padding: '5px 10px', borderRadius: '4px', border: '1px solid #ddd' }}
                    >
                        <option value={5}>5개</option>
                        <option value={10}>10개</option>
                        <option value={20}>20개</option>
                    </select>
                </label>
            </div>

            {/* 게시글 목록 */}
            <div className="board-list">
                {loading ? (
                    <div style={{ textAlign: 'center', padding: '40px' }}>
                        로딩 중...
                    </div>
                ) : boards.length === 0 ? (
                    <div style={{ textAlign: 'center', padding: '40px' }}>
                        게시글이 없습니다.
                    </div>
                ) : (
                    <table>
                        <thead>
                            <tr>
                                <th style={{ width: '10%' }}>번호</th>
                                <th style={{ width: '40%' }}>제목</th>
                                <th style={{ width: '15%' }}>작성자</th>
                                <th style={{ width: '20%' }}>작성일</th>
                                <th style={{ width: '15%' }} className="text-center">관리</th>
                            </tr>
                        </thead>
                        <tbody>
                            {boards.map((board) => (
                                <tr key={board.id}>
                                    <td>{board.id}</td>
                                    <td>
                                        <Link to={`/boards/${board.id}`}>
                                            {board.title}
                                        </Link>
                                    </td>
                                    <td>{board.author}</td>
                                    <td>{formatDate(board.createdAt)}</td>
                                    <td className="text-center">
                                        <Link
                                            to={`/boards/${board.id}/edit`}
                                            className="btn btn-sm btn-edit"
                                        >
                                            수정
                                        </Link>
                                        <button
                                            className="btn btn-sm btn-delete"
                                            onClick={() => handleDelete(board.id)}
                                        >
                                            삭제
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>

            {/* 페이징 */}
            {totalPages > 0 && (
                <div className="pagination">
                    <div className="pagination-info">
                        <span>총 <strong>{totalElements}</strong>개</span>
                        <span>
                            페이지 <strong>{currentPage + 1}</strong> / <strong>{totalPages}</strong>
                        </span>
                    </div>
                    <div className="pagination-controls">
                        {/* 이전 페이지 */}
                        {currentPage > 0 ? (
                            <button
                                className="page-arrow"
                                onClick={() => setCurrentPage(currentPage - 1)}
                            >
                                ‹
                            </button>
                        ) : (
                            <span className="page-arrow disabled">‹</span>
                        )}

                        {/* 페이지 번호 */}
                        {getPageNumbers().map((pageNum) => (
                            <button
                                key={pageNum}
                                className={`page-number ${pageNum === currentPage ? 'active' : ''}`}
                                onClick={() => setCurrentPage(pageNum)}
                            >
                                {pageNum + 1}
                            </button>
                        ))}

                        {/* 다음 페이지 */}
                        {currentPage < totalPages - 1 ? (
                            <button
                                className="page-arrow"
                                onClick={() => setCurrentPage(currentPage + 1)}
                            >
                                ›
                            </button>
                        ) : (
                            <span className="page-arrow disabled">›</span>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
}

export default BoardList;
