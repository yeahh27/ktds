<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<script src="/HelloSpring/js/jquery-3.3.1.min.js" type="text/javascript"></script>
<script type="text/javascript">
	$().ready(function() {
		$(".btnLogin").click(function() {
			$.post("", )
		})
	})
</script>
</head>
<body>
	<h1>회원 로그인하기</h1>
	<form:form id="loginData" modelAttribute="memberVO" action="/HelloSpring/member/login" method="post">
		<div class="errors">
			<ul>
				<li><form:errors path="email" /></li>
				<li><form:errors path="password" /></li>
			</ul>
		</div>
		<div>
			<input type="email" name="email" placeholder="Email" value="${memberVO.email}"/>
		</div>
		<div>
			<input type="password" name="password" placeholder="Password" value="${memberVO.password}" />	
		</div>
		<div>
			<!-- <input type="submit" value="로그인" /> -->
			<input type="button" class="btnLogin" value="로그인" />
		</div>
	</form:form>
</body>
</html>