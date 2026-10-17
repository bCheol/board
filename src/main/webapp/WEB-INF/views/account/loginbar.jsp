<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="currentPath" value="${pageContext.request.servletPath}" />
<c:if test="${currentPath != '/WEB-INF/views/account/login.jsp'
        && currentPath != '/WEB-INF/views/account/register.jsp'
        && currentPath != '/WEB-INF/views/account/find_id.jsp'
        && currentPath != '/WEB-INF/views/account/find_password.jsp'}">

    <div class="account-bar">
        <sec:authorize access="isAnonymous()">
            <a href="${pageContext.request.contextPath}/account/login" class="btn btn-primary">로그인</a>
        </sec:authorize>
        <sec:authorize access="isAuthenticated()">
            <sec:authorize access="hasAnyRole('LOWER_ADMIN', 'UPPER_ADMIN')">
                <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-secondary">관리자 페이지</a>
            </sec:authorize>
            <a href="${pageContext.request.contextPath}/account/mypage" class="account-name">
                <sec:authentication property="principal" var="userDetails" />
                ${userDetails.nickName}님
            </a>
            <form action="${pageContext.request.contextPath}/account/logout" method="post" style="display:inline;">
                  <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                  <button type="submit" class="btn btn-secondary">로그아웃</button>
            </form>
        </sec:authorize>
    </div>
</c:if>