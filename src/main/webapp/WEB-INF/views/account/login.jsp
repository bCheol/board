<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="로그인" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="auth-page">
    <div class="auth-box">
        <h2 class="page-title">로그인</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-error"><c:out value="${error}"/></div>
        </c:if>

        <c:if test="${not empty message}">
            <div class="alert alert-info"><c:out value="${message}"/></div>
        </c:if>

        <form id="login-form" action="${pageContext.request.contextPath}/account/login" method="post" class="board-form auth-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="form-row">
                <label for="accountId">아이디</label>
                <input type="text" id="accountId" name="username" maxlength="15" autofocus>
            </div>
            <div class="form-row">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password">
            </div>
            <span id="login-error" class="field-hint field-hint-error"></span>
            <button type="submit" class="btn btn-primary auth-submit">로그인</button>
        </form>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/account/find-id">아이디 찾기</a>
            <span class="auth-links-sep">|</span>
            <a href="${pageContext.request.contextPath}/account/find-password">비밀번호 찾기</a>
            <span class="auth-links-sep">|</span>
            <a href="${pageContext.request.contextPath}/account/register">회원가입</a>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/login.js"></script>