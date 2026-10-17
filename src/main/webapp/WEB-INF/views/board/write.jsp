<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="${mode == 'edit' ? '게시글 수정' : '게시글 작성'}" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h2 class="page-title"><c:out value="${pageTitle}" /></h2>

<c:choose>
    <c:when test="${mode == 'edit'}">
        <c:url var="formAction" value="/board/edit/${board.num}/update" />
    </c:when>
    <c:otherwise>
        <c:url var="formAction" value="/board/write" />
    </c:otherwise>
</c:choose>

<c:if test="${not empty message}">
    <div class="alert alert-info"><c:out value="${message}"/></div>
</c:if>

<form id="board-form" action="${formAction}" method="post" class="board-form">
    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    <input type="hidden" name="page" value="${param.page}"/>
    <input type="hidden" name="searchType" value="${param.searchType}"/>
    <input type="hidden" name="keyword" value="${param.keyword}"/>
    <input type="hidden" name="sort" value="${param.sort}"/>

    <div class="form-row">
        <label for="title">제목</label>
        <input type="text" id="title" name="title" maxlength="35" value="${fn:escapeXml(board.title)}">
    </div>

    <div class="form-row">
        <label for="body">내용</label>
        <textarea id="body" name="body" maxlength="100" rows="5"><c:out value="${board.body}"/></textarea>
    </div>

    <div class="button-row">
        <c:choose>
            <c:when test="${mode == 'edit'}">
                <a href="javascript:history.back()" class="btn btn-secondary">취소</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/" class="btn btn-secondary">취소</a>
            </c:otherwise>
        </c:choose>
        <button type="submit" class="btn btn-primary">저장</button>
    </div>
</form>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/write.js"></script>