<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Trang chủ phía admin</title>
</head>
<body>
	<jsp:include page="_header.jsp"></jsp:include>
	<jsp:include page="_menu.jsp"></jsp:include>
	<div align="center">
		<h3>Danh sách các cuốn sách</h3>
		<p style="color: red">${errors }</p>
		<form action="deleteBook" id="deleteBookFromAdminForm" method="post">
			<input type="hidden" name="bookId" id="deleteBookFromAdmin" />
		</form>
		<table border="1">
			<tr>
				<th>Tiêu đề</th>
				<th>Tác giả</th>
				<th>Giá tiền</th>
				<th colspan="2" width="120px">Thao tác</th>
			</tr>
			<c:forEach items="${bookList}" var="book">
				<tr>
					<td>${book.title}</td>
					<td>${book.author}</td>
					<td>
					<fmt:setLocale value="en_US" />
					<fmt:formatNumber type="number" maxFractionDigits="0" value="${book.price }" /><sup>đ</sup></td>
					<td align="center"><button type="button"
							onclick="activeAsLink('editBook?bookId=${book.bookId}')">Sửa</button></td>
					<td align="center"><button type="button"
							onclick="onClickDeleteBook('${book.title}', ${book.bookId})">Xóa</button></td>
				</tr>
			</c:forEach>
		</table>
		<a href="createBook">Thêm sách mới</a><br>
	</div>
	<script type="text/javascript">
		function onClickDeleteBook(bookTitle, bookId) {
			let c = confirm('Bạn chắc chắn muốn xóa cuốn sách '+bookTitle+'?');
			if(c){
				document.getElementById("deleteBookFromAdmin").value = bookId;
				document.getElementById("deleteBookFromAdminForm").submit();
			}
		}
		function activeAsLink(link){
			window.location = link;
		}
	</script>
</body>
</html>