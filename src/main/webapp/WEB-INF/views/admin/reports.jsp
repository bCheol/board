<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="게시글 관리" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h2 class="page-title">게시글 관리</h2>

<div class="admin-tabs">
    <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-primary">신고 목록</a>
    <sec:authorize access="hasRole('UPPER_ADMIN')">
        <a href="${pageContext.request.contextPath}/admin/members" class="btn btn-secondary">회원 목록</a>
    </sec:authorize>
    <a href="${pageContext.request.contextPath}/admin/scheduler" class="btn btn-secondary">스케줄러 설정</a>
    <sec:authorize access="hasRole('UPPER_ADMIN')">
         <a href="${pageContext.request.contextPath}/admin/setting" class="btn btn-secondary">기타 설정</a>
    </sec:authorize>
</div>

<div class="board-toolbar">
    <form class="search-form" action="${pageContext.request.contextPath}/admin/reports" method="get">
        <select name="status" class="input-search-type">
            <option value="" ${empty cond.status ? 'selected' : ''}>전체</option>
            <option value="pending" ${cond.status == 'pending' ? 'selected' : ''}>미처리</option>
            <option value="done" ${cond.status == 'done' ? 'selected' : ''}>처리완료</option>
        </select>
        <button type="submit" class="btn btn-secondary">검색</button>
    </form>
</div>

<table class="board-table board-table-fixed">
    <colgroup>
        <col style="width:6%">
        <col style="width:15%">
        <col style="width:10%">
        <col style="width:15%">
        <col style="width:11%">
        <col style="width:12%">
        <col style="width:10%">
        <col style="width:11%">
        <col style="width:10%">
    </colgroup>
    <thead>
    <tr>
        <th>번호</th>
        <th>게시글</th>
        <th>신고자 ID</th>
        <th>신고 이유</th>
        <th>신고 일시</th>
        <th>처리 결과</th>
        <th>처리자 ID</th>
        <th>처리 일시</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${empty reports}">
            <tr>
                <td colspan="9" class="empty-row">조회된 신고가 없습니다.</td>
            </tr>
        </c:when>
        <c:otherwise>
            <c:forEach var="report" items="${reports}">
                <tr>
                    <td>${report.num}</td>
                    <td class="cell-left">
                        <c:choose>
                            <c:when test="${not empty report.boardTitle}">
                                <c:set var="reportBoardTitle"><c:out value="${report.boardTitle}" /></c:set>
                                <c:set var="reportBoardBody"><c:out value="${report.boardBody}" /></c:set>
                                <c:set var="reportBoardWriter"><c:out value="${report.boardAccountId}" /></c:set>
                                <button type="button" class="board-title-cell btn-board-detail" data-board-title="${reportBoardTitle}" data-board-writer="${reportBoardWriter}" data-board-body="${reportBoardBody}" title="${reportBoardTitle}">${reportBoardTitle}</button>
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">삭제된 게시글</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:out value="${report.fromId}" /></td>
                    <td class="cell-left">
                        <span class="reason-cell"><c:out value="${report.reason}" /></span>
                    </td>
                    <td><span data-update-tm="${report.requestTm}"></span></td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty report.result}"><c:out value="${report.result}" /></c:when>
                            <c:otherwise><span class="text-muted">미처리</span></c:otherwise>
                        </c:choose>
                    </td>
                    <td><c:out value="${empty report.adminId ? '-' : report.adminId}" /></td>
                    <td><span data-update-tm="${report.responseTm}"></span></td>
                    <td>
                        <%-- 신고 대상 게시글이 삭제된 경우 처리할 대상이 없으므로 버튼을 표시하지 않음 --%>
                        <c:choose>
                            <c:when test="${not empty report.boardTitle}">
                                <c:set var="reportTitle"><c:out value="${report.boardTitle}" /></c:set>
                                <button type="button"
                                        class="btn ${empty report.result ? 'btn-primary' : 'btn-secondary'} btn-report-process"
                                        data-num="${report.num}"
                                        data-title="${reportTitle}"
                                        data-result="${report.result}">
                                    ${empty report.result ? '처리' : '재처리'}
                                </button>
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted">-</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                </tr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<c:if test="${not empty reports}">
    <c:url var="pageUrlBase" value="/admin/reports">
        <c:param name="status" value="${cond.status}" />
    </c:url>
    <div class="pagination">
        <c:if test="${pageInfo.hasPrevBlock}">
            <a href="${pageUrlBase}&page=${pageInfo.startPage - 1}" class="page-link">이전</a>
        </c:if>
        <c:forEach var="p" begin="${pageInfo.startPage}" end="${pageInfo.endPage}">
            <a href="${pageUrlBase}&page=${p}" class="page-link ${p == pageInfo.page ? 'active' : ''}">${p}</a>
        </c:forEach>
        <c:if test="${pageInfo.hasNextBlock}">
            <a href="${pageUrlBase}&page=${pageInfo.endPage + 1}" class="page-link">다음</a>
        </c:if>
    </div>
</c:if>

<%-- 게시글 자세히 모달 --%>
<div id="report-board-modal-overlay" class="modal-overlay">
    <div class="modal-box">
        <h3 class="modal-title" id="report-board-title"></h3>
        <p id="report-board-body" class="report-reason-full"></p>
        <div class="button-row">
            <span class="board-modal-writer">작성자 아이디 : <span id="report-board-writer"></span></span>
            <button type="button" id="report-board-close-btn" class="btn btn-secondary">닫기</button>
        </div>
    </div>
</div>

<%-- 신고 이유 모달 --%>
<div id="report-reason-modal-overlay" class="modal-overlay">
    <div class="modal-box">
        <h3 class="modal-title">신고 이유</h3>
        <p id="report-reason-full" class="report-reason-full"></p>
        <div class="button-row">
            <button type="button" id="report-reason-close-btn" class="btn btn-secondary">닫기</button>
        </div>
    </div>
</div>

<%-- 신고 처리 모달 --%>
<div id="report-process-modal-overlay" class="modal-overlay" data-context-path="${pageContext.request.contextPath}">
    <div class="modal-box">
        <h3 class="modal-title">신고 처리</h3>
        <form id="report-process-form" method="post" class="board-form">
            <p id="report-process-target"></p>
            <div class="form-row">
                <label for="report-process-result">처리 결과</label>
                <select id="report-process-result" name="result" class="input-search-type">
                    <option value="">선택하세요</option>
                    <option value="반려 (이상 없음)">반려 (이상 없음)</option>
                    <option value="게시글 비공개">게시글 비공개</option>
                </select>
            </div>
            <span id="report-process-error" class="field-hint field-hint-error"></span>
            <div class="button-row">
                <button type="button" id="report-process-cancel-btn" class="btn btn-secondary">취소</button>
                <button type="submit" id="report-process-submit-btn" class="btn btn-primary">저장</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/format.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/admin_reports.js"></script>