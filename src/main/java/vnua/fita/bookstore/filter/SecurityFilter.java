package vnua.fita.bookstore.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import vnua.fita.bookstore.bean.User;
import vnua.fita.bookstore.config.SecurityConfig;
import vnua.fita.bookstore.util.MyUtil;

@WebFilter(filterName = "securityFilter", urlPatterns = { "/*" })
public class SecurityFilter implements Filter{

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;

		String servletPathFull = request.getServletPath();
		String servletPath = MyUtil.getServletPath(servletPathFull);
		//System.out.println("Servlet Path Full: " + servletPathFull);
		
		// Nếu người dùng đang dùng chức năng ko thuộc diện kiểm tra (đăng nhập, quyền), cho họ tiếp tục làm
		if (!SecurityConfig.checkDenyUrlPattern(servletPath)) {
			chain.doFilter(request, response);
			return;
		}

		// Nếu người dùng đã đăng nhập, kiểm tra quyền truy cập servlet
		User loginedUser = MyUtil.getLoginedUser(request.getSession());
		boolean isPermission = false;
		if (loginedUser != null) { 
			byte role = (byte) loginedUser.getRole();
			isPermission = SecurityConfig.checkPermission(role, servletPath);
		}
		
		// Nếu ko có quyền hoặc chưa đăng nhập > cho quay về trang chủ phía khách hàng
		if(!isPermission) {
			response.sendRedirect(request.getContextPath() + "/");
			return;
		}

		chain.doFilter(request, response);

		
	}
	
}
