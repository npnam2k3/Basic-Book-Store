package vnua.fita.bookstore.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter(filterName = "encodingFilter", urlPatterns = {"/*"})
public class EncodingFilter implements Filter{

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		//mã hóa dữ liệu trong request trước khi gửi tới ServletRequest
		request.setCharacterEncoding("UTF-8");
		
		//mã hóa dữ liệu trong response trước khi trả lại cho máy khách
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		
		//Chuyển tiếp yêu cầu đến Servlet tiếp theo trong chuỗi
		chain.doFilter(request, response);
	}
	
}
