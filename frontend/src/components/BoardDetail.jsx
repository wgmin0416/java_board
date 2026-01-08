import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getBoard, deleteBoard } from '../api/boardApi';
import '../style.css';

/**
 * BoardDetail: 게시글 상세 컴포넌트
 * 
 * 기능:
 * - 게시글 상세 정보 표시
 * - 게시글 삭제
 */
function BoardDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [board, setBoard] = useState(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        fetchBoard();
    }, [id]);

    const fetchBoard = async () => {
        setLoading(true);
        try {
            const data = await getBoard(id);
            setBoard(data);
        } catch (error) {
            console.error('게시글 조회 실패:', error);
            alert('게시글을 불러오는데 실패했습니다.');
            navigate('/boards');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async () => {
        if (!window.confirm('정말 삭제하시겠습니까?')) {
            return;
        }

        try {
            await deleteBoard(id);
            alert('게시글이 삭제되었습니다.');
            navigate('/boards');
        } catch (error) {
            console.error('게시글 삭제 실패:', error);
            alert('게시글 삭제에 실패했습니다.');
        }
    };

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

    if (loading) {
        return (
            <div className="container">
                <div style={{ textAlign: 'center', padding: '40px' }}>로딩 중...</div>
            </div>
        );
    }

    if (!board) {
        return (
            <div className="container">
                <div style={{ textAlign: 'center', padding: '40px' }}>게시글을 찾을 수 없습니다.</div>
            </div>
        );
    }

    return (
        <div className="container">
            <header>
                <h1>게시판</h1>
                <div className="header-actions">
                    <Link to="/boards" className="btn btn-secondary">
                        목록
                    </Link>
                </div>
            </header>

            <div className="board-detail">
                <div className="detail-header">
                    <h2>{board.title}</h2>
                    <div className="detail-meta">
                        <span>
                            작성자: <strong>{board.author}</strong>
                        </span>
                        <span>
                            작성일: <strong>{formatDate(board.createdAt)}</strong>
                        </span>
                        {board.updatedAt && board.updatedAt !== board.createdAt && (
                            <span>
                                수정일: <strong>{formatDate(board.updatedAt)}</strong>
                            </span>
                        )}
                    </div>
                </div>

                <div className="detail-content">
                    <pre>{board.content}</pre>
                </div>

                <div className="form-actions">
                    <Link to={`/boards/${id}/edit`} className="btn btn-edit">
                        수정
                    </Link>
                    <button className="btn btn-delete" onClick={handleDelete}>
                        삭제
                    </button>
                    <Link to="/boards" className="btn btn-secondary">
                        목록
                    </Link>
                </div>
            </div>
        </div>
    );
}

export default BoardDetail;
