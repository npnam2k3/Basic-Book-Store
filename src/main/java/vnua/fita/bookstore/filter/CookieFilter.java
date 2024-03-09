package vnua.fita.bookstore.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import vnua.fita.bookstore.bean.User;
import vnua.fita.bookstore.model.UserDAO;
import vnua.fita.bookstore.util.MyUtil;

@WebFilter(filterName = "cookieFilter", urlPatterns = {"/*"})
public class CookieFilter implements Filter{
	private UserDAO userDAO;
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String jdbcURL = filterConfig.getServletContext().getInitParameter("jdbcURL");
		String jdbcUsername = filterConfig.getServletContext().getInitParameter("jdbcUsername");
		String jdbcPassword = filterConfig.getServletContext().getInitParameter("jdbcPassword");
		userDAO = new UserDAO(jdbcURL, jdbcUsername, jdbcPassword);
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request2 = (HttpServletRequest) request;
		HttpSession session = request2.getSession();
		User userInSession = MyUtil.getLoginedUser(session);
		if(userInSession != null) {
			session.setAttribute("CHECKED_COOKIE", "CHECKED");
			chain.doFilter(request2, response);
			return;
		}
		
		String checked = (String)session.getAttribute("CHECKED_COOKIE");
		if(checked == null) {
			String userName = MyUtil.getUserNameInCookie(request2);
			if(userName != null && !userName.isEmpty()) {
				User user = userDAO.findUser(userName);
				if(user != null) {
					String token = MyUtil.getTokenInCookie(request2);
					if(token.equals(MyUtil.createTokenFromUserInfo(user))) {
						MyUtil.storeLoginedUser(session, user);
						session.setAttribute("CHECKED_COOKIE", "CHECKED");
					}
				}
			}
		}
		chain.doFilter(request2, response);
	}
	
}
