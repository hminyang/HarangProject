package bamboo.model;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bamboo.CommandInterface;
import harang.dbcp.DBConnectionMgr;

/**
 * 
 * 사용자가 대나무숲 게시판에서 자신이 작성한 글을 삭제 할 때 필요한 클래스
 * 
 * @author 김민준 KIM MIN JOON
 *
 */
public class U_Bb_Delete_Command implements CommandInterface {

	private Connection con;
	private PreparedStatement pstmt;
	private DBConnectionMgr pool;

	public U_Bb_Delete_Command() {
		try {
			pool = DBConnectionMgr.getInstance();
		} catch (Exception err) {
			System.out.println("DBCP 연결실패 : " + err);
		}
	}

	public String processCommand(HttpServletRequest req, HttpServletResponse resp) {


		String bb_num = req.getParameter("bb_num");
		String sql = "delete from harang.tbl_bamboo where bb_num = ?";

		try {
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, bb_num);
			pstmt.executeUpdate();
		} catch (Exception err) {
			System.out.println("U_Br_Delete_Command에서 에러 : ");
			err.printStackTrace();
		} finally {
			pool.freeConnection(con, pstmt);
		}

		//System.out.println(req.getParameter("bb_num"));
		
		Bb_List_Command goback = new Bb_List_Command();		
		String url = goback.processCommand(req, resp);
		
		return url;
	}

}
