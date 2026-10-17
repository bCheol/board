<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="회원가입" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="auth-page">
    <div class="auth-box auth-box-wide">
        <h2 class="page-title">회원가입</h2>

        <c:if test="${not empty error}">
            <div class="alert alert-error"><c:out value="${error}"/></div>
        </c:if>

        <form id="register-form" action="${pageContext.request.contextPath}/account/register" method="post" class="board-form auth-form">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <div class="form-row">
                <label for="accountId">아이디</label>
                <div class="input-with-btn">
                    <input type="text" id="accountId" name="accountId" maxlength="15">
                    <button type="button" id="check-id-btn" class="btn btn-secondary">중복확인</button>
                </div>
                <span id="check-id-result" class="field-hint"></span>
            </div>
            <div class="form-row">
                <label for="nickName">닉네임</label>
                <div class="input-with-btn">
                    <input type="text" id="nickName" name="nickName" maxlength="10">
                    <button type="button" id="check-nickname-btn" class="btn btn-secondary">중복확인</button>
                </div>
                <span id="check-nickname-result" class="field-hint"></span>
            </div>
            <div class="form-row">
                <label for="password">비밀번호</label>
                <input type="password" id="password" name="password">
            </div>
            <div class="form-row">
                <label for="passwordConfirm">비밀번호 확인</label>
                <input type="password" id="passwordConfirm" name="passwordConfirm">
            </div>
            <div class="form-row">
                <label for="name">이름</label>
                <input type="text" id="name" name="name" maxlength="10">
            </div>
            <div class="form-row">
                <label for="phone">전화번호</label>
                <input type="text" id="phone" name="phone" maxlength="11" placeholder="01012345678">
            </div>
            <span id="register-error" class="field-hint field-hint-error"></span>
            <button type="submit" class="btn btn-primary auth-submit">가입하기</button>
        </form>

        <div class="auth-links">
            <a href="${pageContext.request.contextPath}/account/login">로그인으로 돌아가기</a>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/register.js"></script>