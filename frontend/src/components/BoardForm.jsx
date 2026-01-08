import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getBoard, createBoard, updateBoard } from '../api/boardApi';
import '../style.css';

/**
 * BoardForm: 게시글 작성/수정 폼 컴포넌트
 * 
 * 기능:
 * - 게시글 작성
 * - 게시글 수정
 */
function BoardForm() {
    const { id } = useParams();
    const navigate = useNavigate();
    const isEdit = !!id;
    
    const [formData, setFormData] = useState({
        title: '',
        content: '',
        author: '',
    });
    const [loading, setLoading] = useState(false);
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        if (isEdit) {
            fetchBoard();
        }
    }, [id]);

    const fetchBoard = async () => {
        setLoading(true);
        try {
            const data = await getBoard(id);
            setFormData({
                title: data.title || '',
                content: data.content || '',
                author: data.author || '',
            });
        } catch (error) {
            console.error('게시글 조회 실패:', error);
            alert('게시글을 불러오는데 실패했습니다.');
            navigate('/boards');
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!formData.title.trim() || !formData.content.trim() || !formData.author.trim()) {
            alert('모든 필드를 입력해주세요.');
            return;
        }

        setSubmitting(true);
        try {
            if (isEdit) {
                await updateBoard(id, {
                    title: formData.title,
                    content: formData.content,
                });
                alert('게시글이 수정되었습니다.');
                navigate(`/boards/${id}`);
            } else {
                await createBoard(formData);
                alert('게시글이 작성되었습니다.');
                navigate('/boards');
            }
        } catch (error) {
            console.error('게시글 저장 실패:', error);
            alert(`게시글 ${isEdit ? '수정' : '작성'}에 실패했습니다.`);
        } finally {
            setSubmitting(false);
        }
    };

    if (loading) {
        return (
            <div className="container">
                <div style={{ textAlign: 'center', padding: '40px' }}>로딩 중...</div>
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

            <div className="board-form">
                <h2>{isEdit ? '게시글 수정' : '게시글 작성'}</h2>
                
                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label htmlFor="title">제목</label>
                        <input
                            type="text"
                            id="title"
                            name="title"
                            value={formData.title}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="author">작성자</label>
                        <input
                            type="text"
                            id="author"
                            name="author"
                            value={formData.author}
                            onChange={handleChange}
                            required
                            disabled={isEdit} // 수정 시 작성자 변경 불가
                        />
                    </div>

                    <div className="form-group">
                        <label htmlFor="content">내용</label>
                        <textarea
                            id="content"
                            name="content"
                            rows="15"
                            value={formData.content}
                            onChange={handleChange}
                            required
                        />
                    </div>

                    <div className="form-actions">
                        <button
                            type="submit"
                            className="btn btn-primary"
                            disabled={submitting}
                        >
                            {submitting ? '저장 중...' : isEdit ? '수정' : '작성'}
                        </button>
                        <Link to={isEdit ? `/boards/${id}` : '/boards'} className="btn btn-secondary">
                            취소
                        </Link>
                    </div>
                </form>
            </div>
        </div>
    );
}

export default BoardForm;
