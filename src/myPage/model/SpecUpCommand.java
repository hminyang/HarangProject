package myPage.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import dto.CertiDTO;
import dto.CertiMemberDTO;
import dto.MemberDTO;
import dto.PagingDto;
import harang.dbcp.DBConnectionMgr;
import myPage.CommandInterface;
import util.PagingBean;
/**
 * 스펙 업! 관련 페이지 커멘드 클래스
 * @author 나현기
 *
 */
public class SpecUpCommand implements CommandInterface {

	// DB 커넥션 3 대장
	Connection con;
	PreparedStatement pstmt;
	DataSource ds;
	ResultSet rs;
	// DBCP 사용
	DBConnectionMgr pool;

	@Override
	public Object processCommand(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	
		listCommand(request);

		return "/WEB-INF/myPage/specUp.jsp";
	}

	/**
	 * 스펙 목록과 등록 관련 정보를 불러오는 메소드
	 * @param request 서블릿 리퀘스트
	 */
	public void listCommand(HttpServletRequest request) {
		
		MemberDTO member = (MemberDTO)request.getSession().getAttribute("member");
		String m_id = member.getM_id();
		
		pool = DBConnectionMgr.getInstance();

		String sql;

		ArrayList list = new ArrayList();

		String keyword = request.getParameter("keyword");
		String keyfield = request.getParameter("keyfield");

		
		if (null == (keyfield)) {
			sql = "select c.c_num, c.c_name, c.c_agency, c.c_point, cm.cm_iscomplete, cm.cm_completedate, cm.cm_regdate, cm.cm_image "
					+ "from tbl_certificate c left outer join tbl_certi_member cm on c.c_num = cm.c_num  and cm.m_id = ?";

		} else {
			sql = "select c.c_num, c.c_name, c.c_agency, c.c_point cm.cm_iscomplete, cm.cm_completedate, cm.cm_regdate, cm.cm_image "
					+ "from tbl_certificate c left outer join tbl_certi_member cm on c.c_num = cm.c_num  and cm.m_id = ? "
					+ "where " + keyword + " like '%" + keyfield + "%'";
		}

		try {

			con = pool.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, m_id);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				CertiDTO dto = new CertiDTO();

				dto.setC_num(rs.getString("c_num"));
				dto.setC_name(rs.getString("c_name"));
				dto.setC_agency(rs.getString("c_agency"));
				dto.setC_point(rs.getInt("c_point"));
				dto.setCm_iscomplete(rs.getString("cm_iscomplete"));
				dto.setCm_completedate(rs.getString("cm_completedate"));
				dto.setCm_regdate(rs.getString("cm_regdate"));
				dto.setCm_image(rs.getString("cm_image"));
				
				list.add(dto);

			}
		} catch (Exception err) {
			System.out.println(err);
		} finally {
			// DBCP 접속해제
			pool.freeConnection(con, pstmt, rs);
		}

		request.setAttribute("keyword", keyword);
		request.setAttribute("keyfield", keyfield);
		request.setAttribute("aspeclist", list);

		// 페이징 관련 parameter 받아오기
		int nowPage = 0, nowBlock = 0;
		if (request.getParameter("nowPage") != null) {
			nowPage = Integer.parseInt(request.getParameter("nowPage"));
		}
		if (request.getParameter("nowBlock") != null) {
			nowBlock = Integer.parseInt(request.getParameter("nowBlock"));
		}
		// DB 연동 함수를 쓰기 위해 인스턴스 생성

		PagingBean pbean = new PagingBean();
		// 페이징 관련 정보 셋팅 , 두번째 parameter는 한페이지에 들어갈 글의 개수!!
		PagingDto paging = pbean.Paging(list.size(), 10, nowPage, 10, nowBlock);

		request.setAttribute("paging", paging);
	}
	
	

}
