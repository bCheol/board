<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${pageContext.request.contextPath}" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="auth-page">
    <div class="auth-box">
        <h2 class="page-title">연결할 수 없습니다.</h2>
        <p>다시 시도해 주세요.</p>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>