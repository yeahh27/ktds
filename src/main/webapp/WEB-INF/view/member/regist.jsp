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
		$("#email").keyup(function() {
			// AJAX 요청
			// $.post("URL", 요청 파라미터 (항상 객체 리터럴 방식으로), function(response) {})
			$.post("/HelloSpring/member/check/duplicate"		// URL
					, {											// Request Parameter
						"email" : $(this).val()
					}, function(response) {						// Response Call back
						console.log(response)
						if(response.duplicated) {
							$("#email-error").slideDown(100);
						} else {
							$("#email-error").slideUp(100);
						}
					})
		})
		
		$(".btnReg").click(function () {
			$.post("/HelloSpring/member/regist"
					, $("#regData").serialize()
					, function (response) {
						if(response.status) {
							alert("회원 등록되었습니다.")
							location.href = "/HelloSpring/member/login"
						} else {
							alert("회원 등록에 실패하였습니다.")
							location.href = "/HelloSpring/member/regist"	
						}
					})

		})
	})
</script>
</head>
<body>
	<h1>회원 등록하기</h1>
	<form:form id="regData" modelAttribute="memberVO" action="/HelloSpring/member/regist" method="post" autocomplete="off">
		<div class="errors">
			<ul>
				<li><form:errors path="email" /></li>
				<li><form:errors path="name" /></li>
				<li><form:errors path="password" /></li>
			</ul>
		</div>
		<div>
			<input type="email" id="email" name="email" placeholder="Email" value="${memberVO.email}"/>
			<div id="email-error" style="display: none;">
				이 이메일은 사용할 수 없습니다.
			</div>
		</div>
		<div>
			<input type="text" name="name" placeholder="Name" value="${memberVO.name}" />	
		</div>
		<div>
			<input type="password" name="password" placeholder="Password" value="${memberVO.password}"/>	
		</div>
		<div>
			<input type="button" class="btnReg" value="등록" />
			<!-- <input type="submit" value="등록" /> -->
		</div>
	</form:form>
</body>
</html>