<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<p style="color: red;">${errors}</p>
	<form action="login" method="post">
		<label for="username">Tài khoản</label><br> <input type="text"
			name="username" id="username" value="${loginForm.username }" ><br>
		<br> <label for="password">Mật khẩu</label><br> <input
			type="password" name="password" id="password" value="${loginForm.password }"><br>
		<br> <input type="submit" value="Đăng nhập"> <a
			href="${pageContext.request.contextPath}/">Bỏ qua</a>
	</form>
</body>
</html>