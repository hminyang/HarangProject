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
 * 사용자가 대나무숲 글에 추천을 눌렀을 때 추천한 것을 취소할 때 필요한 클래스
 * 
 * @author 김민준 KIM MIN JOON
 *
 */
public class Bb_Like_Cancle_Command implements CommandInterface {

	private Connection con;
	private PreparedStatement pstmt;
	private DBConnectionMgr pool;

	public Bb_Like_Cancle_Command() {
		try {
			pool = DBConnectionMgr.getInstance();
		} catch (Exception err) {
			System.out.println("DBCP 연결실패 : " + err);
		}
	}

	public String processCommand(HttpServletRequest req, HttpServletResponse resp) {

		HttpSession session = req.getSession();
		MemberDTO mdto = (MemberDTO)session.getAttribute("member");
		
		

		String m_id = mdto.getM_id();
		String bb_num = req.getParameter("bb_num");
		String sql = "DELETE FROM tbl_like WHERE bb_num=? and m_id=?";

		
		
		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bb_num);
			pstmt.setString(2, m_id);
			pstmt.executeUpdate();
		} catch (Exception err) {
			System.out.println("Bb_Like_Cancle_Command에서 에러 : ");
			err.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}

		//System.out.println(req.getParameter("bb_num"));
		
		U_Bb_Content_Command goback = new U_Bb_Content_Command();		
		String url = goback.processCommand(req, resp);
		
		return url;
	}

}
