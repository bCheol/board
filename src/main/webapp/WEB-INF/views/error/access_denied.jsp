<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="접근 권한 없음" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="auth-page">
    <div class="auth-box">
        <h2 class="page-title">접근 권한이 없습니다</h2>
        <p>이 페이지에 접근할 수 있는 권한이 없습니다.</p>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>