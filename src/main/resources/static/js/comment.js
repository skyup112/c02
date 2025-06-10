// /public/static/js/comment.js

// 댓글 목록을 불러오는 함수
function loadComments(postId, page = 1) {
    currentPage = page;
    const commentListDiv = $('#commentList');
    commentListDiv.empty(); // 기존 댓글 목록 비우기
    $('#commentPagination').empty(); // 기존 페이지네이션 비우기
    $('#commentCount').text(0); // 댓글 개수 초기화

    $.ajax({
        url: `/api/comments/post/${postId}`,
        type: 'GET',
        data: { page: page, size: 10 }, // 한 페이지에 10개 댓글 표시
        success: function(response) {
            // response 객체는 BoardPageResponseDTO 형태입니다.
            const comments = response.dtoList;
            const totalElements = response.totalElements;

            $('#commentCount').text(totalElements); // 댓글 개수 업데이트

            if (comments && comments.length > 0) {
                comments.forEach(function(comment) {
                    const formattedDate = new Date(comment.createdAt).toLocaleString('ko-KR', {
                        year: 'numeric', month: '2-digit', day: '2-digit',
                        hour: '2-digit', minute: '2-digit'
                    });

                    // 현재 로그인된 사용자와 댓글 작성자가 동일한지 확인
                    const isAuthor = (currentLoginId === comment.writerName);

                    const actionsHtml = isAuthor ?
                        `<div class="comment-actions">
                            <button class="btn btn-sm btn-info edit-comment-btn" data-comment-id="${comment.commentId}" data-comment-content="${comment.content}">수정</button>
                            <button class="btn btn-sm btn-danger delete-comment-btn" data-comment-id="${comment.commentId}">삭제</button>
                        </div>` : '';

                    const commentHtml = `
                        <div class="comment-item" id="comment-${comment.commentId}">
                            <div class="comment-header">
                                <div>
                                    <span class="comment-author">${comment.writerName}</span>
                                    <span class="comment-date">${formattedDate}</span>
                                </div>
                            </div>
                            <div class="comment-content-display">${comment.content}</div>
                            ${actionsHtml}
                        </div>
                    `;
                    commentListDiv.append(commentHtml);
                });

                // 댓글 페이지네이션 렌더링 (response 객체 전체 전달)
                renderCommentPagination(response);
            } else {
                commentListDiv.append('<p class="text-center text-muted">아직 댓글이 없습니다.</p>');
            }
        },
        error: function(xhr, status, error) {
            console.error("댓글 불러오기 오류:", error);
            if (xhr.status === 404) {
                commentListDiv.append('<p class="text-center text-danger">게시글을 찾을 수 없습니다.</p>');
            } else {
                commentListDiv.append('<p class="text-center text-danger">댓글을 불러오는 데 실패했습니다.</p>');
            }
        }
    });
}

// 댓글 페이지네이션 렌더링 함수 (response 객체 전체를 인자로 받음)
function renderCommentPagination(response) {
    const paginationUl = $('#commentPagination');
    paginationUl.empty();

    // totalPage 필드가 없거나 1 이하면 페이징 버튼을 그리지 않음
    if (response.totalPage === undefined || response.totalPage <= 1) {
        // totalElements가 size보다 작거나 같으면 페이징 불필요 (추가 조건)
        if (response.totalElements <= response.size) {
            return;
        }
    }

    // '이전' 버튼
    if (response.prev) {
        paginationUl.append(`<li class="page-item"><a class="page-link" href="#" data-page="${response.start - 1}" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>`);
    }

    // 페이지 번호 목록
    response.pageNumList.forEach(pageNum => {
        const activeClass = (pageNum === response.page) ? 'active' : '';
        paginationUl.append(`<li class="page-item ${activeClass}"><a class="page-link" href="#" data-page="${pageNum}">${pageNum}</a></li>`);
    });

    // '다음' 버튼
    if (response.next) {
        paginationUl.append(`<li class="page-item"><a class="page-link" href="#" data-page="${response.end + 1}" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>`);
    }
}

// 댓글 등록 함수
function registerComment(postId, content) {
    const commentData = {
        postId: postId,
        content: content
    };

    $.ajax({
        url: '/api/comments',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(commentData),
        success: function(commentId) {
            alert('댓글이 성공적으로 작성되었습니다.');
            $('#commentContent').val(''); // 입력 필드 초기화
            loadComments(postId, 1); // 댓글 목록 첫 페이지로 새로고침
        },
        error: function(xhr, status, error) {
            console.error("댓글 작성 오류:", xhr.responseText);
            if (xhr.status === 401) {
                alert('로그인이 필요합니다. 로그인 후 다시 시도해주세요.');
                window.location.href = '/member/login';
            } else if (xhr.status === 400) {
                alert(`댓글 작성 실패: ${xhr.responseText}`);
            } else {
                alert('댓글 작성에 실패했습니다.');
            }
        }
    });
}

// 댓글 수정 함수
function updateComment(commentId, content) {
    const commentData = {
        content: content
    };

    $.ajax({
        url: `/api/comments/${commentId}`,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify(commentData),
        success: function() {
            alert('댓글이 성공적으로 수정되었습니다.');
            $('#commentModal').modal('hide'); // 모달 닫기
            loadComments(postId, currentPage); // 현재 페이지 댓글 새로고침
        },
        error: function(xhr, status, error) {
            console.error("댓글 수정 오류:", xhr.responseText);
            if (xhr.status === 401) {
                alert('로그인이 필요합니다. 로그인 후 다시 시도해주세요.');
                window.location.href = '/member/login';
            } else if (xhr.status === 403 || xhr.status === 400) {
                alert(`댓글 수정 실패: ${xhr.responseText}`);
            } else {
                alert('댓글 수정에 실패했습니다.');
            }
        }
    });
}

// 댓글 삭제 함수
function deleteComment(commentId) {
    $.ajax({
        url: `/api/comments/${commentId}`,
        type: 'DELETE',
        success: function() {
            alert('댓글이 성공적으로 삭제되었습니다.');
            loadComments(postId, currentPage); // 현재 페이지 댓글 새로고침
        },
        error: function(xhr, status, error) {
            console.error("댓글 삭제 오류:", xhr.responseText);
            if (xhr.status === 401) {
                alert('로그인이 필요합니다. 로그인 후 다시 시도해주세요.');
                window.location.href = '/member/login';
            } else if (xhr.status === 403 || xhr.status === 400) {
                alert(`댓글 삭제 실패: ${xhr.responseText}`);
            } else {
                alert('댓글 삭제에 실패했습니다.');
            }
        }
    });
}

// DOMContentLoaded 대신 jQuery의 ready 함수 사용
$(document).ready(function() {
    // 페이지 로드 시 첫 페이지 댓글 로드
    // postId와 currentPage는 view.html에서 전역 변수로 선언되어 있어야 합니다.
    loadComments(postId, 1);

    // 댓글 등록 버튼 클릭 이벤트
    $('#registerCommentBtn').on('click', function(event) {
        event.preventDefault(); // 폼 제출 방지
        const content = $('#commentContent').val().trim();
        if (content === '') {
            alert('댓글 내용을 입력해주세요.');
            return;
        }
        registerComment(postId, content);
    });

    // 댓글 수정 버튼 클릭 이벤트 (동적으로 생성되는 요소에 대한 이벤트 위임)
    $(document).on('click', '.edit-comment-btn', function() {
        const commentId = $(this).data('comment-id');
        const commentContent = $(this).data('comment-content');
        $('#editCommentId').val(commentId);
        $('#editCommentContent').val(commentContent);
        $('#commentModal').modal('show');
    });

    // 댓글 수정 완료 버튼 클릭 이벤트
    $('#updateCommentBtn').on('click', function() {
        const commentId = $('#editCommentId').val();
        const content = $('#editCommentContent').val().trim();
        if (content === '') {
            alert('댓글 내용을 입력해주세요.');
            return;
        }
        updateComment(commentId, content);
    });

    // 댓글 삭제 버튼 클릭 이벤트 (동적으로 생성되는 요소에 대한 이벤트 위임)
    $(document).on('click', '.delete-comment-btn', function() {
        if (confirm('정말로 이 댓글을 삭제하시겠습니까?')) {
            const commentId = $(this).data('comment-id');
            deleteComment(commentId);
        }
    });

    // 페이지네이션 클릭 이벤트 (동적으로 생성되는 요소에 대한 이벤트 위임)
    $(document).on('click', '#commentPagination .page-link', function(e) {
        e.preventDefault();
        const page = $(this).data('page');
        if (page) {
            loadComments(postId, page);
        }
    });
});