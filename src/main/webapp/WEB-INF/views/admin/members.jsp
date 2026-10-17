<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="회원 관리" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h2 class="page-title">회원 관리</h2>

<div class="admin-tabs">
    <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-secondary">신고 목록</a>
    <a href="${pageContext.request.contextPath}/admin/members" class="btn btn-primary">회원 목록</a>
    <a href="${pageContext.request.contextPath}/admin/scheduler" class="btn btn-secondary">스케줄러 설정</a>
    <a href="${pageContext.request.contextPath}/admin/setting" class="btn btn-secondary">기타 설정</a>
</div>

<div class="board-toolbar">
    <form class="search-form" action="${pageContext.request.contextPath}/admin/members" method="get">
        <select name="roleType" class="input-search-type">
            <option value="" ${empty cond.roleType ? 'selected' : ''}>전체 유형</option>
            <option value="admin" ${cond.roleType == 'admin' ? 'selected' : ''}>관리자</option>
            <option value="user" ${cond.roleType == 'user' ? 'selected' : ''}>일반</option>
        </select>
        <select name="existYn" class="input-search-type">
            <option value="" ${empty cond.existYn ? 'selected' : ''}>전체 상태</option>
            <option value="1" ${cond.existYn == '1' ? 'selected' : ''}>가입</option>
            <option value="0" ${cond.existYn == '0' ? 'selected' : ''}>탈퇴</option>
        </select>
        <select name="blockYn" class="input-search-type">
            <option value="" ${empty cond.blockYn ? 'selected' : ''}>전체 차단여부</option>
            <option value="1" ${cond.blockYn == '1' ? 'selected' : ''}>차단</option>
            <option value="0" ${cond.blockYn == '0' ? 'selected' : ''}>미차단</option>
        </select>
        <input type="text" name="keyword" class="input-keyword" placeholder="아이디를 입력하세요" value="${fn:escapeXml(cond.keyword)}">
        <button type="submit" class="btn btn-secondary">검색</button>
    </form>
</div>

<table class="board-table">
    <colgroup>
        <col style="width:13%">
        <col style="width:17%">
        <col style="width:15%">
        <col style="width:11%">
        <col style="width:11%">
        <col style="width:10%">
        <col style="width:11%">
        <col style="width:12%">
    </colgroup>
    <thead>
    <tr>
        <th>회원 유형</th>
        <th>아이디</th>
        <th>닉네임</th>
        <th>탈퇴 여부</th>
        <th>신고된 횟수</th>
        <th>차단 여부</th>
        <th>관리</th>
        <th>권한 변경</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${empty members}">
            <tr>
                <td colspan="8" class="empty-row">조회된 회원이 없습니다.</td>
            </tr>
        </c:when>
        <c:otherwise>
            <c:forEach var="member" items="${members}">
                <tr>
                    <td><c:out value="${member.roleType}" /></td>
                    <td><c:out value="${member.accountId}" /></td>
                    <td><c:out value="${member.nickName}" /></td>
                    <td>${member.exist == 0 ? '탈퇴' : '가입'}</td>
                    <td>${member.warningCount}</td>
                    <td>${member.block == 1 ? '차단' : '미차단'}</td>
                    <td>
                       <c:if test="${member.exist != 0 and member.accountId != pageContext.request.userPrincipal.name}">
                            <c:choose>
                                <c:when test="${member.block == 1}">
                                    <button type="button" class="btn btn-secondary btn-block-action"
                                            data-account-id="${fn:escapeXml(member.accountId)}" data-action="unblock">해제</button>
                                </c:when>
                                <c:otherwise>
                                    <button type="button" class="btn btn-danger btn-block-action"
                                            data-account-id="${fn:escapeXml(member.accountId)}" data-action="block">차단</button>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                    </td>
                    <td>
                        <c:if test="${member.exist != 0 and member.block != 1 and member.accountId != pageContext.request.userPrincipal.name}">
                            <button type="button" class="btn btn-secondary btn-role-action"
                                    data-account-id="${fn:escapeXml(member.accountId)}" data-current-role="${fn:escapeXml(member.role)}">권한 변경</button>
                        </c:if>
                    </td>
                </tr>
            </c:forEach>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>

<c:if test="${not empty members}">
    <c:url var="pageUrlBase" value="/admin/members">
        <c:param name="roleType" value="${cond.roleType}" />
        <c:param name="existYn" value="${cond.existYn}" />
        <c:param name="blockYn" value="${cond.blockYn}" />
        <c:param name="keyword" value="${cond.keyword}" />
    </c:url>
    <div class="pagination">
        <c:if test="${pageInfo.hasPrevBlock}">
            <a href="${pageUrlBase}&page=${pageInfo.startPage - 1}" class="page-link">이전</a>
        </c:if>
        <c:forEach var="p" begin="${pageInfo.startPage}" end="${pageInfo.endPage}">
            <a href="${pageUrlBase}&page=${p}" class="page-link ${p == pageInfo.page ? 'active' : ''}">${p}</a>
        </c:forEach>
        <c:if test="${pageInfo.hasNextBlock}">
            <a href="${pageUrlBase}&page=${pageInfo.endPage + 1}" class="page-link">다음</a>
        </c:if>
    </div>
</c:if>

<%-- 회원 차단/해제 모달 --%>
<div id="block-modal-overlay" class="modal-overlay" data-context-path="${pageContext.request.contextPath}">
    <div class="modal-box">
        <h3 class="modal-title" id="block-modal-title">회원 차단</h3>
        <form id="block-form" method="post" class="board-form">
            <input type="hidden" name="accountId" id="block-account-id">
            <p id="block-message"></p>
            <div class="form-row">
                <label for="block-password">관리자 비밀번호</label>
                <input type="password" id="block-password" name="password" autocomplete="current-password">
            </div>
            <span id="block-password-error" class="field-hint field-hint-error"></span>
            <div class="button-row">
                <button type="button" id="block-cancel-btn" class="btn btn-secondary">취소</button>
                <button type="submit" id="block-submit-btn" class="btn btn-danger">차단</button>
            </div>
        </form>
    </div>
</div>

<%-- 권한 변경 모달 --%>
<div id="role-modal-overlay" class="modal-overlay" data-context-path="${pageContext.request.contextPath}">
    <div class="modal-box">
        <h3 class="modal-title">권한 변경</h3>
        <form id="role-form" method="post" class="board-form">
            <input type="hidden" name="accountId" id="role-account-id">
            <p id="role-message"></p>
            <div class="form-row">
                <label for="role-select">권한</label>
                <select id="role-select" name="role" class="input-search-type">
                    <option value="USER">일반</option>
                    <option value="LOWER_ADMIN">하위 관리자</option>
                    <option value="UPPER_ADMIN">상위 관리자</option>
                </select>
            </div>
            <div class="form-row">
                <label for="role-password">관리자 비밀번호</label>
                <input type="password" id="role-password" name="password" autocomplete="current-password">
            </div>
            <span id="role-password-error" class="field-hint field-hint-error"></span>
            <div class="button-row">
                <button type="button" id="role-cancel-btn" class="btn btn-secondary">취소</button>
                <button type="submit" id="role-submit-btn" class="btn btn-primary">변경</button>
            </div>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/admin_members.js"></script>