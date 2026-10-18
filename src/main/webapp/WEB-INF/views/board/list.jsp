<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="게시판 목록" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:if test="${not empty message}">
    <div class="alert alert-info"><c:out value="${message}"/></div>
</c:if>

<div class="board-toolbar">
    <form class="search-form" action="${pageContext.request.contextPath}/" method="get">
        <select name="sort" class="input-sort">
            <option value="latest" ${cond.sort == 'latest' ? 'selected' : ''}>최신순</option>
            <option value="view" ${cond.sort == 'view' ? 'selected' : ''}>조회수순</option>
        </select>
        <select name="searchType" class="input-search-type">
            <option value="all" ${cond.searchType == 'all' ? 'selected' : ''}>제목+내용</option>
            <option value="title" ${cond.searchType == 'title' ? 'selected' : ''}>제목</option>
            <option value="body" ${cond.searchType == 'body' ? 'selected' : ''}>내용</option>
        </select>
        <input type="text" name="keyword" class="input-keyword" placeholder="검색어를 입력하세요" value="${fn:escapeXml(cond.keyword)}">
        <button type="submit" class="btn btn-secondary">검색</button>
    </form>
    <sec:authorize access="isAuthenticated() and hasRole('USER')">
        <a href="${pageContext.request.contextPath}/board/write" class="btn btn-primary">글쓰기</a>
    </sec:authorize>
</div>

<table class="board-table board-table-fixed">
    <colgroup>
        <col style="width:50%">
        <col style="width:15%">
        <col style="width:20%">
        <col style="width:15%">
    </colgroup>
    <thead>
    <tr>
        <th>제목</th>
        <th>작성자</th>
        <th>작성일</th>
        <th>조회수</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${empty boards}">
            <tr>
                <td colspan="4" class="empty-row">등록된 게시글이 없습니다.</td>
            </tr>
        </c:when>
        <c:otherwise>
            <c:forEach var="board" items="${boards}">
                <tr>
                    <td class="title-cell">
                        <c:url var="detailUrl" value="/board/detail/${board.num}">
                            <c:param name="page" value="${pageInfo.page}" />
                            <c:param name="searchType" value="${cond.searchType}" />
                            <c:param name="keyword" value="${cond.keyword}" />
                            <c:param name="sort" value="${cond.sort}" />
                        </c:url>
                        <a class="board-title-cell" href="${detailUrl}"><c:out value="${board.title}"/></a>
                    </td>
                    <td><c:out value="${board.writerNickName}" /></td>
                    <td data-create-tm="${board.createTm}"></td>
                    <td>${board.viewCount}</td>
                </tr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<c:if test="${not empty boards}">
    <c:url var="pageUrlBase" value="/">
        <c:param name="searchType" value="${cond.searchType}" />
        <c:param name="keyword" value="${cond.keyword}" />
        <c:param name="sort" value="${cond.sort}" />
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

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/format.js"></script>