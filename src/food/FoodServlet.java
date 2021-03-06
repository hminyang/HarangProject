package food;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.LoginBean;

/**
 * 하랑레스토랑(관리자 포함) 관련 서블릿
 * @author 나현기
 *
 */
@WebServlet("/food")
public class FoodServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		// TODO Auto-generated method stub
		String cmd = request.getParameter("cmd");
		String url = null;
		
		CommandInterface command = null;
		
		//요거 한줄이면 아~~주 깔끔하게 떨어진다. if문도 사라짐
		command = CommandFactory.newInstance().createCommand(cmd);
		
		//중복되는 새로 고침을 막기 위함
		String state =(String)request.getSession().getAttribute("STATE");
		
		url = (String)command.processCommand(request, response);

		//세션을 리프레쉬
		LoginBean refresh = new LoginBean();
		refresh.refreshSession(request);
		
		RequestDispatcher view = request.getRequestDispatcher(url);
		view.forward(request, response);
		
	}
}
