<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="게시글 상세" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:if test="${not empty message && not blocked}">
    <div class="alert alert-info"><c:out value="${message}"/></div>
</c:if>

<sec:authorize access="isAuthenticated()">
    <sec:authentication property="principal" var="user" />
</sec:authorize>
<c:set var="canReport" value="${not empty user
        and user.role != 'UPPER_ADMIN'
        and user.role != 'LOWER_ADMIN'
        and user.username != board.accountId}" />

<c:url var="listUrl" value="/">
    <c:param name="page" value="${empty param.page ? 1 : param.page}" />
    <c:param name="searchType" value="${param.searchType}" />
    <c:param name="keyword" value="${param.keyword}" />
    <c:param name="sort" value="${param.sort}" />
</c:url>

<c:url var="editUrl" value="${pageContext.request.contextPath}/board/edit/${board.num}">
    <c:param name="page" value="${param.page}" />
    <c:param name="searchType" value="${param.searchType}" />
    <c:param name="keyword" value="${param.keyword}" />
    <c:param name="sort" value="${param.sort}" />
</c:url>

<c:choose>
    <c:when test="${blocked}">
        <div class="board-detail">
            <div class="alert alert-warning">관리자에 의해 비공개 처리된 게시글입니다.</div>
            <div class="button-row">
                <a href="${listUrl}" class="btn btn-secondary">목록</a>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="board-detail">
            <c:if test="${blockedNotice}">
                <div class="alert alert-warning">비공개 처리된 게시글입니다. 관리자만 열람할 수 있습니다.</div>
            </c:if>
            <h2 class="detail-title"><c:out value="${board.title}" /></h2>
            <div class="detail-meta">
                <span>작성자 : <c:out value="${board.writerNickName}" /></span>
                <span>작성일 : <span data-create-tm="${board.createTm}"></span></span>
                <span>수정일 : <span data-update-tm="${board.updateTm}"></span></span>
                <span>조회수 : ${board.viewCount}</span>
                <c:if test="${canReport}">
                    <button type="button" id="report-open-btn" class="report-btn" data-board-num="${board.num}">신고</button>
                </c:if>
            </div>
            <div class="detail-body"><c:out value="${board.body}" /></div>
            <div class="button-row">
                <a href="${listUrl}" class="btn btn-secondary">목록</a>
                <c:if test="${not empty pageContext.request.userPrincipal && pageContext.request.userPrincipal.name == board.accountId}">
                    <a href="${editUrl}" class="btn btn-primary">수정</a>
                    <form id="delete-form" action="${pageContext.request.contextPath}/board/delete/${board.num}" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <input type="hidden" name="page" value="${param.page}"/>
                        <button type="submit" class="btn btn-danger">삭제</button>
                    </form>
                </c:if>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<c:if test="${not blocked && not empty board && canReport}">
    <div id="report-modal-overlay" class="modal-overlay">
        <div class="modal-box">
            <h3 class="modal-title">게시글 신고</h3>
            <div class="board-form">
                <div class="form-row">
                    <label for="report-reason">신고 이유</label>
                    <textarea id="report-reason" rows="4" maxlength="50"
                              placeholder="신고 이유를 입력하세요. (최대 50자)"></textarea>
                </div>
                <span id="report-error" class="field-hint field-hint-error"></span>
            </div>
            <div class="button-row">
                <button type="button" id="report-cancel-btn" class="btn btn-secondary">취소</button>
                <button type="button" id="report-submit-btn" class="btn btn-danger">신고</button>
            </div>
        </div>
    </div>
</c:if>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/format.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/detail.js"></script>