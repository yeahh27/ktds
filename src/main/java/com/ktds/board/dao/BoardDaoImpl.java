package com.ktds.board.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ktds.board.vo.BoardSearchVO;
import com.ktds.board.vo.BoardVO;
import com.ktds.common.dao.support.BindData;
import com.ktds.member.vo.MemberVO;

@Repository
public class BoardDaoImpl implements BoardDao {
	
	// Inner Class (Inner Interface)
	private interface Query {
		 int INSERT_QUERY = 0;
	     int SELECT_QUERY = 1;
	     int DELETE_QUERY = 2;
	     int SELECT_ALL_QUERY = 3;
	} 
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Resource(name="boardQueries")
	private List<String> boardQueries;
	
	@Override
	public int insertBoard(BoardVO boardVO) {
		return this.jdbcTemplate.update(
					this.boardQueries.get(Query.INSERT_QUERY), 
					boardVO.getSubject(), boardVO.getContent(), boardVO.getEmail()
					, boardVO.getFileName(), boardVO.getOriginFileName()
				);
	}

	@Override
	public int updateBoard(BoardVO boardVO) {
		System.out.println("Call updateBoard();");
		return 0;
	}

	@Override
	public BoardVO selectOneBoard(int id) {
		return this.jdbcTemplate.queryForObject(this.boardQueries.get(Query.SELECT_QUERY), new Object[] {id}, new RowMapper<BoardVO>() {

			@Override
			public BoardVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				/*BoardVO boardVO = new BoardVO();
				boardVO.setId(id);
				boardVO.setSubject(rs.getString("SUBJECT"));
				boardVO.setContent(rs.getString("CONTENT"));
				boardVO.setEmail(rs.getString("EMAIL"));
				boardVO.setCrtDt(rs.getString("CRT_DT"));
				boardVO.setMdfyDt(rs.getString("MDFT_DT"));
				boardVO.setFileName(rs.getString("FILE_NAME"));
				boardVO.setOriginFileName(rs.getString("ORIGIN_FILE_NAME"));
				return boardVO;*/
				//return BindData.bindData(rs, new BoardVO());		// 강사님이 만든 유틸리티
				MemberVO memberVO = BindData.bindData(rs, new MemberVO());
				BoardVO boardVO = BindData.bindData(rs, new BoardVO());
				boardVO.setMemberVO(memberVO);
				return boardVO;
			}
		});
	}

	@Override
	public int deleteOneBoard(int id) {
		return this.jdbcTemplate.update(this.boardQueries.get(Query.DELETE_QUERY), id);
	}

	@Override
	//public List<BoardVO> selectAllBoards() {
	public List<BoardVO> selectAllBoards(BoardSearchVO boardSearchVO) {
		return this.jdbcTemplate.query(this.boardQueries.get(Query.SELECT_ALL_QUERY), new RowMapper<BoardVO>() {

			@Override
			public BoardVO mapRow(ResultSet rs, int rowNum) throws SQLException {
				MemberVO memberVO = BindData.bindData(rs, new MemberVO());
				BoardVO boardVO = BindData.bindData(rs, new BoardVO());
				boardVO.setMemberVO(memberVO);
				return boardVO;
			}
		});
	}
	
	@Override
	public int selectAllBoardsCount(BoardSearchVO boardSearchVO) {
		return 0;
	}
	
}
