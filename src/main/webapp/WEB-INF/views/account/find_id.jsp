<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="아이디 찾기" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="auth-page">
    <div class="auth-box">
        <h2 class="page-title">아이디 찾기</h2>

        <c:if test="${searched}">
            <c:choose>
                <c:when test="${not empty foundId}">
                    <div class="alert alert-info"><strong><c:out value="${foundId}" /></strong></div>
                </c:when>
                <c:when test="${not empty message}">
                    <div class="alert alert-info"><strong><c:out value="${message}" /></strong></div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-error">일치하는 회원 정보를 찾을 수 없습니다.</div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <form id="find-id-form" action="${pageContext.request.contextPath}/account/find-id" method="post" class="board-form auth-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="form-row">
                <label for="name">이름</label>
                <input type="text" id="name" name="name" maxlength="5" autofocus>
            </div>
            <div class="form-row">
                <label for="phone">전화번호</label>
                <input type="text" id="phone" name="phone" maxlength="11" placeholder="01012345678">
            </div>
            <span id="find-id-error" class="field-hint field-hint-error"></span>
            <button type="submit" class="btn btn-primary auth-submit">아이디 찾기</button>
        </form>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/account/login">로그인으로 돌아가기</a>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/find_id.js"></script>