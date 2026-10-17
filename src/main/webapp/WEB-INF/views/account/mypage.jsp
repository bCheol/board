<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="내 정보" />
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<c:if test="${not empty message}">
    <div class="alert alert-info"><c:out value="${message}"/></div>
</c:if>

<div class="mypage-box">
    <h2 class="page-title">내 정보</h2>
        <table class="info-table">
            <tr>
                <th>아이디</th>
                <td><c:out value="${account.accountId}" /></td>
            </tr>
            <tr>
                <th>비밀번호</th>
                <td>
                    <span class="field-value"></span>
                    <button type="button" class="btn btn-secondary btn-inline-edit"
                            data-field="password" data-label="비밀번호" data-type="password" data-maxlength="70">수정</button>
                </td>
            </tr>
            <tr>
                <th>닉네임</th>
                <td>
                    <span class="field-value"><c:out value="${account.nickName}" /></span>
                    <button type="button" class="btn btn-secondary btn-inline-edit"
                            data-field="nickName" data-label="닉네임" data-type="text"
                            data-maxlength="10" data-value="${fn:escapeXml(account.nickName)}">수정</button>
                </td>
            </tr>
            <tr>
                <th>이름</th>
                <td>
                    <span class="field-value"><c:out value="${account.name}" /></span>
                    <button type="button" class="btn btn-secondary btn-inline-edit"
                            data-field="name" data-label="이름" data-type="text"
                            data-maxlength="5" data-value="${account.name}">수정</button>
                </td>
            </tr>
            <tr>
                <th>전화번호</th>
                <td>
                    <span class="field-value"><c:out value="${account.phone}" /></span>
                    <button type="button" class="btn btn-secondary btn-inline-edit"
                            data-field="phone" data-label="전화번호" data-type="text"
                            data-maxlength="11" data-value="${account.phone}">수정</button>
                </td>
            </tr>
            <tr>
                <th>회원 탈퇴</th>
                <td>
                    <span class="field-value withdraw-desc">탈퇴 시 같은 정보로 가입할 수 없습니다.</span>
                    <button type="button" id="withdraw-open-btn" class="btn btn-danger">탈퇴</button>
                </td>
            </tr>
        </table>
    </div>

    <form id="withdraw-form" action="${pageContext.request.contextPath}/account/withdraw" method="post" style="display:none;">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
    </form>

    <div id="edit-modal-overlay" class="modal-overlay">
        <div class="modal-box">
            <h3 class="modal-title" id="edit-modal-title">정보 수정</h3>
            <form id="edit-form" action="${pageContext.request.contextPath}/account/mypage/update" method="post" class="board-form">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input type="hidden" id="edit-field" name="field">
                <div class="form-row" id="edit-value-row">
                    <label for="edit-value" id="edit-value-label">값</label>
                    <div class="input-with-btn">
                        <input type="text" id="edit-value" name="value">
                        <button type="button" id="edit-check-nickname-btn" class="btn btn-secondary" style="display:none;">중복확인</button>
                    </div>
                    <span id="edit-value-error" class="field-hint field-hint-error"></span>
                </div>
                <div id="edit-password-rows" style="display:none;">
                    <div class="form-row">
                        <label for="edit-current-password">현재 비밀번호</label>
                        <input type="password" id="edit-current-password" name="currentPassword" autocomplete="current-password">
                    </div>
                    <div class="form-row">
                        <label for="edit-new-password">새 비밀번호</label>
                        <input type="password" id="edit-new-password" autocomplete="new-password">
                    </div>
                    <div class="form-row">
                        <label for="edit-new-password-confirm">새 비밀번호 확인</label>
                        <input type="password" id="edit-new-password-confirm" name="newPasswordConfirm" autocomplete="new-password">
                    </div>
                    <span id="edit-password-error" class="field-hint field-hint-error"></span>
                </div>
                <div class="button-row">
                    <button type="button" id="edit-cancel-btn" class="btn btn-secondary">취소</button>
                    <button type="submit" class="btn btn-primary">저장</button>
                </div>
            </form>
        </div>
    </div>

    <div id="withdraw-modal-overlay" class="modal-overlay">
        <div class="modal-box">
            <div id="withdraw-step-password">
                <h3 class="modal-title">비밀번호 확인</h3>
                <div class="board-form">
                    <div class="form-row">
                        <label for="withdraw-password">현재 비밀번호</label>
                        <input type="password" id="withdraw-password" autocomplete="current-password" autofocus>
                    </div>
                </div>
                <div id="withdraw-password-error" class="alert alert-error" style="display:none;"></div>
                <div class="button-row">
                    <button type="button" id="withdraw-cancel-btn-1" class="btn btn-secondary">취소</button>
                    <button type="button" id="withdraw-verify-btn" class="btn btn-primary">확인</button>
                </div>
            </div>
            <div id="withdraw-step-confirm" style="display:none;">
                <h3 class="modal-title">회원 탈퇴</h3>
                <p>탈퇴 후 재가입할 수 없습니다.</p>
                <p>정말 탈퇴하시겠습니까?</p>
                <div class="button-row">
                    <button type="button" id="withdraw-cancel-btn-2" class="btn btn-secondary">취소</button>
                    <button type="button" id="withdraw-final-btn" class="btn btn-danger">탈퇴</button>
                </div>
            </div>
        </div>
    </div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
<script src="${pageContext.request.contextPath}/resources/js/mypage.js"></script>