package vnua.fita.bookstore.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vnua.fita.bookstore.bean.Book;
import vnua.fita.bookstore.bean.CartItem;
import vnua.fita.bookstore.bean.Order;
import vnua.fita.bookstore.bean.User;
import vnua.fita.bookstore.util.Constant;
import vnua.fita.bookstore.util.MyUtil;

public class OrderDAO {
	private String jdbcURL;
	private String jdbcUsername;
	private String jdbcPassword;
	private Connection jdbcConnection;
	private Statement statement;
	private PreparedStatement preStatement;
	private ResultSet resultSet;

	public OrderDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
		super();
		this.jdbcURL = jdbcURL;
		this.jdbcUsername = jdbcUsername;
		this.jdbcPassword = jdbcPassword;
	}

	// trả lại kết quả check và cập nhật số lượng có trong kho của các mặt hàng
	// trong order
	public boolean checkAndUpdateAvaiableBookOfOrder(Order order) {
		boolean checkAvaiable = true; // có hàng
		String sql;
		List<CartItem> orderBookList = order.getOrderBookList();
		try {
			jdbcConnection = DBConnection.createConnection(jdbcURL, jdbcUsername,
					jdbcPassword);
			for (CartItem cartItem : orderBookList) {
				sql = "SELECT quantity_in_stock FROM tblbook WHERE book_id=?";
				preStatement = jdbcConnection.prepareStatement(sql);
				Book selectedBook = cartItem.getSelectedBook();
				preStatement.setInt(1, selectedBook.getBookId());
				resultSet = preStatement.executeQuery();
				if (resultSet.next()) {
					int presentQuantityInStock = resultSet.getInt("quantity_in_stock"); // số
																						// lượng
																						// hiện
																						// có
																						// trong
																						// kho
					if (presentQuantityInStock < cartItem.getQuantity()) { // nếu nhỏ hơn
																			// số lượng
																			// đặt mua
						checkAvaiable = false;
						selectedBook.setQuantityInStock(presentQuantityInStock);
						// cập nhật số lượng có trong kho của sách
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(resultSet);
			DBConnection.closePreparedStatement(preStatement);
			DBConnection.closeConnect(jdbcConnection);
		}
		return checkAvaiable;
	}

	public boolean insertOrder(Order order) {
		boolean insertResult = false;
		int orderId = -1;
		String orderNo = null;

		String sql1 = "INSERT INTO tblorder(customer_username, payment_mode, order_status, total_cost, payment_img, payment_status, delivery_address, order_approve_date, status_date, order_date) VALUES(?,?,?,?,?,?,?,?,?,?)";
		String sql2 = "SELECT max(order_id) from tblorder";
		String sql3 = "UPDATE tblorder SET order_no = ? WHERE order_id = ?";
		String sql4 = "INSERT INTO tblorder_book(book_id, order_id, quantity, price) VALUES(?,?,?,?)";
		String sql5 = "UPDATE tblbook SET quantity_in_stock = ? WHERE book_id=?";

		jdbcConnection = DBConnection.createConnection(jdbcURL, jdbcUsername,
				jdbcPassword);
		try {
			jdbcConnection.setAutoCommit(false);

			// thêm hóa đơn
			preStatement = jdbcConnection.prepareStatement(sql1);
			preStatement.setString(1, order.getCustomer().getUsername());
			preStatement.setString(2, order.getPaymentMode());
			preStatement.setByte(3, order.getOrderStatus());
			preStatement.setFloat(4, order.getTotalCost());
			preStatement.setString(5, order.getPaymentImagePath());
			preStatement.setBoolean(6, order.isPaymentStatus());
			preStatement.setString(7, order.getDeliveryAddress());
			if (order.getOrderApproveDate() != null) {
				preStatement.setString(8,
						MyUtil.convertDateToString(order.getOrderApproveDate()));
			} else {
				preStatement.setString(8, null);
			}
			preStatement.setString(9, MyUtil.convertDateToString(order.getStatusDate()));
			preStatement.setString(10, MyUtil.convertDateToString(order.getOrderDate()));
			insertResult = preStatement.executeUpdate() > 0;

			// Lấy order_id của hóa đơn vừa tạo
			statement = jdbcConnection.createStatement();
			resultSet = statement.executeQuery(sql2);
			if (resultSet.next()) {
				orderId = resultSet.getInt(1);

				// nếu là hình thức thanh toán khi nhận hàng, tạo luôn orderNo từ orderId
				// vừa tạo
				if ("cash".equals(order.getPaymentMode())) {
					preStatement = jdbcConnection.prepareStatement(sql3);
					orderNo = MyUtil.createOrderNo(orderId);
					preStatement.setString(1, orderNo);
					preStatement.setInt(2, orderId);
					insertResult = preStatement.executeUpdate() > 0;
					if (!insertResult) {
						throw new SQLException();
					}
				}
				if ("transfer".equals(order.getPaymentMode())) {
					preStatement = jdbcConnection.prepareStatement(sql3);
					orderNo = MyUtil.createOrderNo(orderId);
					preStatement.setString(1, orderNo);
					preStatement.setInt(2, orderId);
					insertResult = preStatement.executeUpdate() > 0;
					if (!insertResult) {
						throw new SQLException();
					}
				}
				List<CartItem> orderBookList = order.getOrderBookList();
				for (CartItem cartItem : orderBookList) {
					// thêm các cuốn sách trong hóa đơn vào bảng tblorder_book
					preStatement = jdbcConnection.prepareStatement(sql4);
					preStatement.setInt(1, cartItem.getSelectedBook().getBookId());
					preStatement.setInt(2, orderId);
					preStatement.setInt(3, cartItem.getQuantity());
					preStatement.setInt(4, cartItem.getSelectedBook().getPrice());
					insertResult = preStatement.executeUpdate() > 0;
					if (!insertResult) {
						throw new SQLException();
					}

					// cập nhật số lượng trong bảng tblbook
					preStatement = jdbcConnection.prepareStatement(sql5);
					preStatement.setInt(1, cartItem.getSelectedBook().getQuantityInStock()
							- cartItem.getQuantity());
					preStatement.setInt(2, cartItem.getSelectedBook().getBookId());
					insertResult = preStatement.executeUpdate() > 0;
					if (!insertResult) {
						throw new SQLException();
					}
				}
				jdbcConnection.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(resultSet);
			DBConnection.closePreparedStatement(preStatement);
			DBConnection.closeConnect(jdbcConnection);
		}
		if (insertResult) {
			// thêm thành công, bổ sung thuộc tính vào đối tượng order và trả lại
			order.setOrderId(orderId);
			order.setOrderNo(orderNo);
		}
		return insertResult;
	}

	// lay danh sach hoa don theo username cua khach hang
	public List<Order> getOrderList(String customerUserName) {
		// key: orderId, value: order
		Map<Integer, Order> orderListHashMap = new HashMap<Integer, Order>();
		String sql = "SELECT ord.*, ordb.quantity, ordb.price, b.*, u.* "
				+ "FROM tblorder ord "
				+ "INNER JOIN tblorder_book ordb ON ord.order_id = ordb.order_id "
				+ "INNER JOIN tblbook b ON ordb.book_id = b.book_id "
				+ "INNER JOIN tbluser u ON ord.customer_username = u.user_name "
				+ "WHERE ord.customer_username = ? " + "ORDER BY ord.order_date DESC";
		jdbcConnection = DBConnection.createConnection(jdbcURL, jdbcUsername,
				jdbcPassword);
		try {
			preStatement = jdbcConnection.prepareStatement(sql);
			preStatement.setString(1, customerUserName);
			resultSet = preStatement.executeQuery();
			while (resultSet.next()) {
				int orderId = resultSet.getInt("order_id");
				if (!orderListHashMap.containsKey(orderId)) {
					// neu chua co trong hashmap
					Order order = new Order();
					fillOrderFromResultSet(resultSet, order);
					List<CartItem> orderBookList = new ArrayList<CartItem>();
					Book orderBook = new Book();
					fillBookFromResultSet(resultSet, orderBook);
					CartItem cartItem = new CartItem(orderBook,
							resultSet.getInt("ordb.quantity"));
					orderBookList.add(cartItem);
					order.setOrderBookList(orderBookList);
					orderListHashMap.put(orderId, order);
				} else {
					// neu orderId da co trong hashmap > update order tai vi tri da ton
					// tai do
					Order order = orderListHashMap.get(orderId); // lay ra doi tuong order
					List<CartItem> orderBookList = order.getOrderBookList(); // lay ra
																				// bookList
					Book orderBook = new Book();
					fillBookFromResultSet(resultSet, orderBook);
					CartItem cartItem = new CartItem(orderBook,
							resultSet.getInt("ordb.quantity"));
					orderBookList.add(cartItem); // bo sung them vao danh sach order tuong
													// ung
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(resultSet);
			DBConnection.closePreparedStatement(preStatement);
			DBConnection.closeConnect(jdbcConnection);
		}

		Collection<Order> values = orderListHashMap.values();
		ArrayList<Order> orderList = new ArrayList<Order>(values);
		// Sap xep theo orderId giam dan, da dinh nghia trong phuong thuc compareTo cua
		// lop Order
		return orderList;
	}

	private void fillOrderFromResultSet(ResultSet resultSet, Order order)
			throws SQLException {
		order.setOrderId(resultSet.getInt("order_id"));
		order.setOrderNo(resultSet.getString("ord.order_no"));
		order.setOrderDate(resultSet.getTimestamp("ord.order_date"));
		order.setOrderApproveDate(resultSet.getTimestamp("ord.order_approve_date"));
		order.setOrderStatus(resultSet.getByte("ord.order_status"));
		order.setStatusDate(resultSet.getTimestamp("ord.status_date"));
		order.setPaymentMode(resultSet.getString("ord.payment_mode"));
		order.setPaymentStatus(resultSet.getBoolean("ord.payment_status"));
		order.setTotalCost(resultSet.getInt("ord.total_cost"));
		order.setDeliveryAddress(resultSet.getString("ord.delivery_address"));
		order.setPaymentImagePath(resultSet.getString("ord.payment_img"));
		User customer = new User();
		customer.setUsername(resultSet.getString("u.user_name"));
		customer.setFullname(resultSet.getString("u.fullname"));
		customer.setMobile(resultSet.getString("u.mobile"));
		order.setCustomer(customer);
	}

	private void fillBookFromResultSet(ResultSet resultSet, Book orderBook)
			throws SQLException {
		orderBook.setBookId(resultSet.getInt("b.book_id"));
		orderBook.setTitle(resultSet.getString("b.title"));
		orderBook.setAuthor(resultSet.getString("b.author"));
		orderBook.setPrice(resultSet.getInt("ordb.price"));
		orderBook.setImagePath(resultSet.getString("b.image_path"));
	}

	// lay danh sach hoa don theo trang thai (danh cho admin)
	public List<Order> getOrderList(byte orderStatus) {
		// key: orderId, value: order
		Map<Integer, Order> orderListHashMap = new HashMap<Integer, Order>();
		String sql = "SELECT ord.*, ordb.quantity, ordb.price, b.*, u.* "
				+ "FROM tblorder ord "
				+ "INNER JOIN tblorder_book ordb ON ord.order_id = ordb.order_id "
				+ "INNER JOIN tblbook b ON ordb.book_id = b.book_id "
				+ "INNER JOIN tbluser u ON ord.customer_username = u.user_name "
				+ "WHERE ord.order_status = ? "
				+ "ORDER BY ord.status_date DESC, ord.order_date DESC";
		jdbcConnection = DBConnection.createConnection(jdbcURL, jdbcUsername,
				jdbcPassword);
		try {
			preStatement = jdbcConnection.prepareStatement(sql);
			preStatement.setByte(1, orderStatus);
			resultSet = preStatement.executeQuery();
			while (resultSet.next()) {
				int orderId = resultSet.getInt("order_id");
				if (!orderListHashMap.containsKey(orderId)) {
					// neu chua co trong HashMap
					Order order = new Order();
					fillOrderFromResultSet(resultSet, order);
					List<CartItem> orderBookList = new ArrayList<CartItem>();
					Book orderBook = new Book();
					fillBookFromResultSet(resultSet, orderBook);
					CartItem cartItem = new CartItem(orderBook,
							resultSet.getInt("ordb.quantity"));
					orderBookList.add(cartItem);
					order.setOrderBookList(orderBookList);
					orderListHashMap.put(orderId, order);
				} else {
					// neu orderId da co trong hashmap > update order tai vi tri da ton
					// tai do
					Order order = orderListHashMap.get(orderId);
					List<CartItem> orderBookList = order.getOrderBookList();
					Book orderBook = new Book();
					fillBookFromResultSet(resultSet, orderBook);
					CartItem cartItem = new CartItem(orderBook,
							resultSet.getInt("ordb.quantity"));
					orderBookList.add(cartItem);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closeResultSet(resultSet);
			DBConnection.closePreparedStatement(preStatement);
			DBConnection.closeConnect(jdbcConnection);
		}
		Collection<Order> values = orderListHashMap.values();
		ArrayList<Order> orderList = new ArrayList<Order>(values);
		return orderList;
	}

	// dung cho truong hop: hoan tat giao hang, huy don, tra hang
	public boolean updateOrder(int orderId, byte orderStatus) {
		boolean updateResult = false;

		String sql = "UPDATE tblorder SET order_status = ?, status_date = ?, payment_status = ? "
				+ "WHERE order_id = ?";
		Date statusDate = new Date();
		jdbcConnection = DBConnection.createConnection(jdbcURL, jdbcUsername,
				jdbcPassword);
		try {
			preStatement = jdbcConnection.prepareStatement(sql);
			preStatement.setByte(1, orderStatus);
			preStatement.setString(2, MyUtil.convertDateToString(statusDate));
			preStatement.setBoolean(3,
					(Constant.DELIVERED_ORDER_STATUS == orderStatus) ? true : false);
			preStatement.setInt(4, orderId);
			updateResult = preStatement.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBConnection.closePreparedStatement(preStatement);
			DBConnection.closeConnect(jdbcConnection);
		}
		return updateResult;
	}

	public boolean updateOrderNo(int orderId, byte orderStatus) {
		boolean updateResult = false;
		String sql = "UPDATE tblorder SET order_no = ?, order_approve_date = ?, order_status = ?, status_date = ?, payment_status = ? "
				+ "WHERE order_id = ?";
		jdbcConnection = DBConnection.createConnection(jdbcURL, jdbcUsername, jdbcPassword);
		Date now = new Date();
		String orderNo = MyUtil.createOrderNo(orderId);
		try {
			preStatement = jdbcConnection.prepareStatement(sql);
			preStatement.setString(1, orderNo);
			preStatement.setString(2, MyUtil.convertDateToString(now));
			preStatement.setByte(3, orderStatus);
			preStatement.setString(4, MyUtil.convertDateToString(now));
			preStatement.setBoolean(5, true);
			preStatement.setInt(6, orderId);
			updateResult = preStatement.executeUpdate() > 0;
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			DBConnection.closePreparedStatement(preStatement);
			DBConnection.closeConnect(jdbcConnection);
		}
		return updateResult;
	}
}
