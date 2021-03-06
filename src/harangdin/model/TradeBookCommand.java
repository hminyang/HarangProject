package harangdin.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import dto.BookDTO;
import dto.MemberDTO;
import dto.RecordDTO;
import harang.dbcp.DBConnectionMgr;
import harangdin.CommandInterface;
import util.LoginBean;
import util.PointBean;

/**
 * 판매자와 구매자와의 거래 중 관리자의 중개
 * @author 서지윤
 *
 */

public class TradeBookCommand implements CommandInterface{

	//DB 커넥션 3 대장
	Connection con;
	PreparedStatement pstmt;
	DataSource ds;
	ResultSet rs;
	//DBCP 사용
	DBConnectionMgr pool;
	@Override
	public Object processCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
		PointBean point = new PointBean();
		
		String b_name = request.getParameter("b_name");
		String b_num = request.getParameter("b_num");
		String m_id = request.getParameter("m_id");
		int r_point = Integer.parseInt(request.getParameter("max_point"));
		String giver_id = maxId(b_num, r_point);
		long giver_point = maxPoint(giver_id);
		
		String r_content = "[판매의사전달]" + b_name + "을" + giver_id +" 에게 판매하고 싶습니다"; 
		System.out.println(r_point);
		System.out.println(giver_point);
		
		
		//1단계입니다
		String result =  point.tradePoint(r_content, giver_point, r_point, giver_id, "admin05");
		
		
		if("complete".equals(result)){
			//2단계입니다
			String result2 = recode(giver_id, b_num, r_point);
			
			if("success".equals(result2)){
				System.out.println("성공입니다");
				tradeCheckBook(b_num);
			}
			request.setAttribute("result", result2);
		}
			
		
		return "/WEB-INF/harangdin/tradeResult.jsp";
	}
	
	/**
	 * 구매자가 제시한 포인트 중 가장 높은 포인트를 제시한 구매자의 학번을 알려줌
	 * @param b_num 책번호
	 * @param bh_want 책포인트
	 * @return 메세지
	 */
	
	public String maxId(String b_num, int bh_want){
		
		String m_id = null;
		
		String sql="SELECT m_id FROM tbl_b_hunter where b_num=? and bh_want=?";
		
		pool = DBConnectionMgr.getInstance();
		
		try {
			
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, b_num);
			pstmt.setInt(2, bh_want);			
			rs = pstmt.executeQuery();
				
			rs.next();
			m_id = rs.getString("m_id");
			
			
		}catch(Exception err){
			System.out.println(err);
		}
		finally{
			// DBCP 접속해제
			pool.freeConnection(con,pstmt,rs);
		}
		return m_id;
	}
	
	/**
	 * 판매자의 총 포인트를 알려준다
	 * @param m_id 학번
	 * @return 포인트
	 */

	public long maxPoint(String m_id){
		
		String sql="SELECT m_point FROM tbl_member WHERE m_id=?";
		
		pool = DBConnectionMgr.getInstance();
		
		long m_point = 0;
		
		
		try {
			
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, m_id);
			rs = pstmt.executeQuery();
				
			rs.next();
			m_point = rs.getLong("m_point");
			
			
		}catch(Exception err){
			System.out.println(err);
		}
		finally{
			// DBCP 접속해제
			pool.freeConnection(con,pstmt,rs);
		}
		
		return m_point;
	}
	
	/**
	 * 구매자가 판매자의 선택을 받을 시, Y가 뜬다
	 * @param giver_id 구매자 학번
	 * @param b_num 책번로
	 * @param bh_want 포인트
	 * @return
	 */
	
	public String recode(String giver_id, String b_num, int bh_want){
		String message = null;
		
		int i = 0;
		
		String sql="UPDATE tbl_b_hunter SET bh_choice='Y' WHERE m_id=? and b_num=? and bh_want=?";
		
		pool = DBConnectionMgr.getInstance();
		
			try {
			
			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, giver_id);
			pstmt.setString(2, b_num);
			pstmt.setInt(3, bh_want);
			i = pstmt.executeUpdate();
			
			System.out.println(i);
			
			if(i == 1 ){
				System.out.println("성공");
				message="success";
			}
			else{
				System.out.println("실패");
				message="fail";
			}
	
			
		}catch(Exception err){
			System.out.println(err);
		}
		finally{
			// DBCP 접속해제
			pool.freeConnection(con,pstmt);
		}
		
		return message;
	}
	
	/**
	 * 판매자와 구매자가 서로 거래중일 때는 '거래중'이라는 내용이 뜬다
	 * @param b_num 책넘버
	 */
	
	public void tradeCheckBook(String b_num){
		
		String sql="UPDATE tbl_book SET b_iscomplete='거래중' WHERE b_num=?";
		
		pool = DBConnectionMgr.getInstance();
		
		try {
		
		con = pool.getConnection();
		pstmt = con.prepareStatement(sql);
		pstmt.setString(1, b_num);
		pstmt.executeUpdate();
		
		
	}catch(Exception err){
		System.out.println(err);
	}
	finally{
		// DBCP 접속해제
		pool.freeConnection(con,pstmt);
	}
	}
}