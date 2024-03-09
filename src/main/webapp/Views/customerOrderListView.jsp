<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="vnua.fita.bookstore.util.Constant"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/bookstore_style.css">
<title>Thông tin tài khoản/đơn hàng</title>
</head>
<body>
	<jsp:include page="_header.jsp"></jsp:include>
	<jsp:include page="_menu.jsp"></jsp:include>
	<div align="center">
		<h3>Xin chào: ${loginedUser.fullname}</h3>
		<table>
			<tr>
				<td><b>Tài khoản:</b> &nbsp;${loginedUser.username}</td>
				<td><b>Số di động:</b> &nbsp;${loginedUser.mobile}</td>
				<td><b>Email:</b> &nbsp;${loginedUser.email}</td>
				<td><b>Địa chỉ:</b> &nbsp;${loginedUser.address}</td>
			</tr>
		</table>

		<hr>

		<div align="center">
			<h3>DANH SÁCH ĐƠN HÀNG</h3>
			<table border="1">
				<tr>
					<th>Mã hóa đơn</th>
					<th>Ngày đặt mua</th>
					<th>Ngày xác nhận</th>
					<th>Địa chỉ nhận sách</th>
					<th>Phương thức thanh toán</th>
					<th>Trạng thái đơn hàng</th>
					<th>Thao tác</th>
				</tr>
				<c:forEach items="${orderListOfCustomer}" var="orderOfCustomer">
					<tr>
						<td>${orderOfCustomer.orderNo}</td>
						<td><fmt:formatDate value="${orderOfCustomer.orderDate}"
								pattern="dd-MM-yyyy HH:mm" /></td>
						<td><fmt:formatDate
								value="${orderOfCustomer.orderApproveDate}"
								pattern="dd-MM-yyyy HH:mm" /></td>
						<td>${orderOfCustomer.deliveryAddress}</td>
						<td>${orderOfCustomer.paymentModeDescription}</td>
						<td>${orderOfCustomer.orderStatusDescription}<c:if
								test="${Constant.WAITING_CONFIRM_ORDER_STATUS != orderOfCustomer.orderStatus}">
								&nbsp;-
								&nbsp;${orderOfCustomer.paymentStatusDescription}
							</c:if>
						</td>
						<td>
							<button
								onclick="document.getElementById('div${orderOfCustomer.orderId}').style.display='block';">Xem
								chi tiết</button>
							<button
								onclick="document.getElementById('div${orderOfCustomer.orderId}').style.display='none';">Ẩn</button>
							<!-- Khối thông tin chi tiết hóa đơn  -->
							<div id="div${orderOfCustomer.orderId}" style="display: none;">
								<h3>Các cuốn sách trong hóa đơn</h3>
								<table border="1">
									<tr style="background-color: yellow;">
										<th>Tiêu đề</th>
										<th>Tác giả</th>
										<th>Giá tiền</th>
										<th>Số lượng mua</th>
										<th>Tổng thành phần</th>
									</tr>
									<c:forEach items="${orderOfCustomer.orderBookList}"
										var="cartItem">
										<tr>
											<td>${cartItem.selectedBook.title}</td>
											<td>${cartItem.selectedBook.author}</td>
											<td><fmt:formatNumber type="number"
													maxFractionDigits="0"
													value="${cartItem.selectedBook.price}"></fmt:formatNumber><sup>đ</sup></td>
											<td>${cartItem.quantity}</td>
											<td><fmt:formatNumber type="number"
													maxFractionDigits="0"
													value="${cartItem.selectedBook.price * cartItem.quantity}"></fmt:formatNumber><sup>đ</sup></td>
										</tr>
									</c:forEach>
								</table>
								<br> Tổng số tiền: <b> <span> <fmt:formatNumber
											type="number" maxFractionDigits="0" value="${orderOfCustomer.totalCost }"></fmt:formatNumber>
								</span> <sup>đ</sup>
								</b>
							</div> <!-- ------------------------------------------------------------- -->
						</td>
					</tr>
				</c:forEach>
			</table>
		</div>
	</div>

</body>
</html>