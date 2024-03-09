package vnua.fita.bookstore.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vnua.fita.bookstore.bean.Order;
import vnua.fita.bookstore.model.BookDAO;
import vnua.fita.bookstore.model.OrderDAO;
import vnua.fita.bookstore.util.Constant;
import vnua.fita.bookstore.util.MyUtil;

/**
 * Servlet implementation class CustomerOrderListServlet
 */
@WebServlet(urlPatterns = {"/customerOrderList"})
public class CustomerOrderListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private OrderDAO orderDAO;
       
	
	public void init() {
		String jdbcURL = getServletContext().getInitParameter("jdbcURL");
		String jdbcPassword = getServletContext().getInitParameter("jdbcPassword");
		String jdbcUsername = getServletContext().getInitParameter("jdbcUsername");
//		bookDAO = new BookDAO("jdbc:mysql://localhost:3306/bookstore", "root", "123456");
		orderDAO = new OrderDAO(jdbcURL, jdbcUsername, jdbcPassword);
	}
   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String customerUserName = MyUtil.getLoginedUser(request.getSession()).getUsername();
		List<Order> orderListOfCustomer = orderDAO.getOrderList(customerUserName);
		request.setAttribute("orderListOfCustomer", orderListOfCustomer);
		RequestDispatcher rd=this.getServletContext().getRequestDispatcher("/Views/customerOrderListView.jsp");
		rd.forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
