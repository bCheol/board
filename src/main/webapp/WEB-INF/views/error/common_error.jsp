<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${pageContext.request.contextPath}" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="auth-page">
    <div class="auth-box">
        <h2 class="page-title">문제가 발생했습니다.</h2>
        <a href="${pageContext.request.contextPath}/">메인 페이로 돌아가기</a>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>