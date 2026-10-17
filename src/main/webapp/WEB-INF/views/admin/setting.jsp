<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="설정 관리" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h2 class="page-title">설정 관리</h2>

<div class="admin-tabs">
    <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-secondary">신고 목록</a>
    <a href="${pageContext.request.contextPath}/admin/members" class="btn btn-secondary">회원 목록</a>
    <a href="${pageContext.request.contextPath}/admin/scheduler" class="btn btn-secondary">스케줄러 설정</a>
    <a href="${pageContext.request.contextPath}/admin/setting" class="btn btn-primary">기타 설정</a>
</div>

<table class="board-table">
    <colgroup>
        <col style="width:20%">
        <col style="width:50%">
        <col style="width:30%">
    </colgroup>
    <thead>
    <tr>
        <th>번호</th>
        <th>설명</th>
        <th>관리</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${empty settingList}">
            <tr>
                <td colspan="4" class="empty-row">조회된 설정이 없습니다.</td>
            </tr>
        </c:when>
        <c:otherwise>
            <c:forEach var="list" items="${settingList}">
                <tr>
                    <td><c:out value="${list.num}" /></td>
                    <td><c:out value="${list.name}" /></td>
                    <td>
                         <button type="button" class="btn btn-secondary btn-manage-action"
                                        data-type="${fn:escapeXml(list.type)}" data-action="${fn:escapeXml(list.name)}">실행하기</button>
                    </td>
                </tr>
             </c:forEach>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<div id="manage-modal-overlay" class="modal-overlay" data-context-path="${pageContext.request.contextPath}">
    <div class="modal-box">
        <h3 class="modal-title" id="manage-modal-title">설정 변경</h3>
        <form id="manage-form" method="post" class="board-form">
            <input type="hidden" name="num" id="manage-type">
            <p id="manage-message"></p>
            <div class="form-row">
                <label for="manage-password">관리자 비밀번호</label>
                <input type="password" id="manage-password" name="password" autocomplete="current-password">
            </div>
            <div class="form-row" id="div-encryptKey">
                <label for="manage-encryptKey">이전 암호키</label>
                <input type="password" id="manage-encryptKey" name="password" autocomplete="current-password">
            </div>
            <span id="manage-info" class="field-hint field-hint-ok"></span>
            <span id="manage-password-error" class="field-hint field-hint-error"></span>
            <div class="button-row">
                <button type="button" id="manage-cancel-btn" class="btn btn-secondary">취소</button>
                <button type="submit" id="manage-submit-btn" class="btn btn-primary">실행</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/admin_setting.js"></script>