<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="_csrf" content="${_csrf.token}">
    <meta name="_csrf_header" content="${_csrf.headerName}">

    <title><c:out value="${pageTitle}" default="게시판" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/board.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/account.css">
</head>
<body>
<div class="wrap">
    <header class="site-header">
        <a href="${pageContext.request.contextPath}/" class="logo">게시판</a>
        <%@ include file="/WEB-INF/views/account/loginbar.jsp" %>
    </header>
    <main class="content">