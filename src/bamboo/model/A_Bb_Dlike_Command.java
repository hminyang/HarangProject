package bamboo.model;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import bamboo.CommandInterface;
import dto.MemberDTO;
import harang.dbcp.DBConnectionMgr;

/**
 * 
 * 관리자가 대나무숲 글에 비추천을 남겼을 경우 그 비추천을 취소할 때 필요한 클래스
 * 
 * @author student
 *
 */
public class A_Bb_Dlike_Command implements CommandInterface {

	private Connection con;
	private PreparedStatement pstmt;
	private DBConnectionMgr pool;

	public A_Bb_Dlike_Command() {
		try {
			pool = DBConnectionMgr.getInstance();
		} catch (Exception err) {
			System.out.println("DBCP 연결실패 : " + err);
		}
	}

	public String processCommand(HttpServletRequest req, HttpServletResponse resp) {

		HttpSession session = req.getSession();
		MemberDTO mdto = (MemberDTO)session.getAttribute("admin");
		
		

		String m_id = mdto.getM_id();
		String bb_num = req.getParameter("bb_num");
		String sql = "insert into harang.tbl_dlike (bb_num, m_id) values (?,?)";

		
		
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bb_num);
			pstmt.setString(2, m_id);
			pstmt.executeUpdate();
		} catch (Exception err) {
			System.out.println("A_Bb_Dlike_Command에서 에러 : ");
			err.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}

		//System.out.println(req.getParameter("bb_num"));
		
		A_Bb_Content_Command goback = new A_Bb_Content_Command();		
		String url = goback.processCommand(req, resp);
		
		return url;
	}

}
