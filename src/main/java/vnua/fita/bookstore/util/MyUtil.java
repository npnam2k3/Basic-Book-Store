package vnua.fita.bookstore.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import vnua.fita.bookstore.bean.Cart;
import vnua.fita.bookstore.bean.CartItem;
import vnua.fita.bookstore.bean.User;

public class MyUtil {
	public static void storeLoginedUser(HttpSession session, User loginedUser) {
		session.setAttribute("loginedUser", loginedUser);
	}
	
	public static User getLoginedUser(HttpSession session) {
		return (User) session.getAttribute("loginedUser");
	}

	// lưu trữ thông tin giỏ hàng vào Session
	public static void storeCart(HttpSession session, Cart cart) {
		// trên JSP có thể truy cập thông qua ${loginedUser}
		session.setAttribute("cartOfCustomer", cart);
	}

	// lấy thông tin giỏ hàng lưu trữ trong Session
	public static Cart getCartOfCustomer(HttpSession session) {
		Cart cartOfCustomer = (Cart) session.getAttribute("cartOfCustomer");
		return cartOfCustomer;
	}

	public static String getPathInfoFromServletPath(String path) {
		if (path == null || path.isEmpty()) {
			return ""; // Hoặc có thể ném một ngoại lệ
		}

		String[] result = path.split("/");
		if (result.length == 0) {
			return "";
		}

		return result[result.length - 1];
	}

	public static String getTimeLabel() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy hh_mm");
		return sdf.format(new Date());
	}

	public static String extracFileExtension(Part part) {
		// form-data; name = "file"; filename = "";
		String contentDisp = part.getHeader("content-disposition");
		int indexOfDot = contentDisp.lastIndexOf(".");
		return contentDisp.substring(indexOfDot, contentDisp.length() - 1); // return .jpg
	}

	public static File getFolderUpload(String appPath, String folderName) {
		// user.dir: thu muc ung dung Web hien tai
		File folderUpload = new File(appPath + File.separator + folderName);
		if (!folderUpload.exists()) {
			folderUpload.mkdirs();
		}
		return folderUpload;
	}

	public static String convertDateToString(Date date) {
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static String convertDateToStringVn(Date date) {
		DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		return sdf.format(date);
	}

	public static void updateCartOfCustomer(HttpSession session,
			Map<Integer, CartItem> cartItemList) {
		Cart cartOfCustomer = getCartOfCustomer(session);
		cartOfCustomer.setCartItemList(cartItemList);
		session.setAttribute("cartOfCustomer", cartOfCustomer);
	}

	public static void deleteCart(HttpSession session) {
		session.removeAttribute("cartOfCustomer");
	}

	public static String createOrderNo(int orderId) {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
		int code = orderId % 100;
		return sdf.format(new Date())+code;
	}

	public static Date subtractFromDate(int months, Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, -3);
		return c.getTime();
	}

	public static String attachTailToDate(String date) {
		return date + " 00:00:00";
	}
}
