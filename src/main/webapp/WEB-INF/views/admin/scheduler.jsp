<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="스케줄러 관리" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h2 class="page-title">스케줄러 관리</h2>

<div class="admin-tabs">
    <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-secondary">신고 목록</a>
    <sec:authorize access="hasRole('UPPER_ADMIN')">
        <a href="${pageContext.request.contextPath}/admin/members" class="btn btn-secondary">회원 목록</a>
    </sec:authorize>
    <a href="${pageContext.request.contextPath}/admin/schedule" class="btn btn-primary">스케줄러 설정</a>
    <sec:authorize access="hasRole('UPPER_ADMIN')">
        <a href="${pageContext.request.contextPath}/admin/setting" class="btn btn-secondary">기타 설정</a>
    </sec:authorize>
</div>

<table class="board-table">
    <colgroup>
        <col style="width:20%">
        <col style="width:40%">
        <col style="width:20%">
        <col style="width:20%">
    </colgroup>
    <thead>
    <tr>
        <th>번호</th>
        <th>스케줄 이름</th>
        <th>실행 주기</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${empty schedulerMap}">
            <tr>
                <td colspan="4" class="empty-row">조회된 스케줄러가 없습니다.</td>
            </tr>
        </c:when>
        <c:otherwise>
            <tr>
                <td><c:out value="${schedulerMap.num}" /></td>
                <td><c:out value="${schedulerMap.name}" /></td>
                <td><c:out value="${schedulerMap.cycle}" /></td>
                <td>
                    <c:if test="${!schedulerMap.state}">
                        <button type="button" class="btn btn-secondary btn-scheduler-action"
                                    data-scheduler-id="${fn:escapeXml(schedulerMap.num)}" data-action="start">실행하기</button>
                    </c:if>
                    <c:if test="${schedulerMap.state}">
                        <button type="button" class="btn btn-secondary btn-scheduler-action"
                                    data-scheduler-id="${fn:escapeXml(schedulerMap.num)}" data-action="stop">중단하기</button>
                    </c:if>
                </td>
            </tr>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<div id="scheduler-modal-overlay" class="modal-overlay" data-context-path="${pageContext.request.contextPath}">
    <div class="modal-box">
        <h3 class="modal-title" id="scheduler-modal-title">스케줄러 실행 변경</h3>
        <form id="scheduler-form" method="post" class="board-form">
            <input type="hidden" name="num" id="scheduler-id">
            <p id="scheduler-message"></p>
            <div class="form-row">
                <label for="scheduler-password">관리자 비밀번호</label>
                <input type="password" id="scheduler-password" name="password" autocomplete="current-password">
            </div>
            <span id="scheduler-password-error" class="field-hint field-hint-error"></span>
            <div class="button-row">
                <button type="button" id="scheduler-cancel-btn" class="btn btn-secondary">취소</button>
                <button type="submit" id="scheduler-submit-btn" class="btn btn-primary">실행</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/admin_scheduler.js"></script>