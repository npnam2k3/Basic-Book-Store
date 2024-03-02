<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Thông tin chi tiết sách</title>
</head>
<body>
	<jsp:include page="_header_backend.jsp"></jsp:include>
	<jsp:include page="_menu_backend.jsp"></jsp:include>
	<div align="center">
		<h3>Thông tin chi tiết cuốn sách</h3>
		<p style="color: red">${errors }</p>
		<c:if test="${not empty book }">
			<form id="detailBookForm" action="cartBook/addToCart" method="post">
				<input type="hidden" name="bookId" value="${book.bookId }">
				<table style="width: 30%;" border="1">
					<tr>
						<td width="25%">Tiêu đề</td>
						<td>${book.title}</td>
					</tr>
					<tr>
						<td>Tác giả</td>
						<td>${book.author}</td>
					</tr>
					<tr>
						<td>Giá tiền</td>
						<td><fmt:formatNumber type="number" value="${book.price }" /><sup>đ</sup></td>
					</tr>
					<tr>
						<td colspan="2">
							<div
								style="text-align: justify; text-justify: inner-word; margin: 5px;display: flex;">
								<div>
									<img style=" margin-right: 5px" alt="Book Image"
										src="${book.imagePath}" width="150">
								</div>
								${book.detail }
							</div>
						</td>
					<tr>
				</table>
				<div style="margin-top: 20px">
					<b>Số lượng có sẵn: <span>${book.quantityInStock}</span></b>
					<%-- &nbsp;&nbsp; <img alt="minus-icon" src="img/icons-minus.png"
						onclick="minusValue('quantity');" width="20"> <input
						type="text" value="1" size="2" style="line-height: 20px;"
						id="quantity" name="quantityPurchased"
						onkeyup="validateValue(this, ${book.quantityInStock});"> <img
						alt="add-icon" src="img/icons-add.png"
						onclick="plusValue('quantity',${book.quantityInStock})" width="20"
						height="20"> &nbsp;&nbsp;&nbsp;
					<c:if test="${not empty loginedUser }">
						<button
							onclick="checkQuantityAndSubmit('quantity',${book.bookId},${bookquantityInStock })">Thêm
							vào giỏ hàng</button>
					</c:if>

					<c:if test="${empty loginedUser }">
						<button type="button" onclick="alert('Bạn cần đăng nhập!')">Thêm
							vào giỏ hàng</button>
					</c:if>
					&nbsp;&nbsp;&nbsp; <a href="clientHome">Tiếp tục xem sách</a> --%>
				</div>
			</form>
		</c:if>
	</div>
</body>
</html>