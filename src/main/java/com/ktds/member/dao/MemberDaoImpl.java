package com.ktds.member.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.ktds.common.dao.support.BindData;
import com.ktds.member.vo.MemberVO;

@Repository
public class MemberDaoImpl implements MemberDao {
	
	private final int INSERT_QUERY = 0;
	private final int SELECT_QUERY = 1;

	private interface Query {
		int POINT_UPDATE_QUERY = 2;
	}
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Resource(name="memberQueries")
	private List<String> memberQueries;
	
	@Override
	public int insertMember(MemberVO memberVO) {
		return this.jdbcTemplate.update(this.memberQueries.get(INSERT_QUERY),
										memberVO.getEmail(), memberVO.getName(), memberVO.getPassword());
	}

	@Override
	public MemberVO selectOneMember(MemberVO memberVO) {
		return this.jdbcTemplate.queryForObject(this.memberQueries.get(SELECT_QUERY),
				new Object[] { memberVO.getEmail(), memberVO.getPassword() }, new RowMapper<MemberVO>() {

					@Override
					public MemberVO mapRow(ResultSet rs, int rowNum) throws SQLException {
						return BindData.bindData(rs, new MemberVO());
					}
		});
		
	}

	@Override
	public int updatePoint(Map<String, Object> param) {		
		return this.jdbcTemplate.update(this.memberQueries.get(Query.POINT_UPDATE_QUERY), param.get("point"), param.get("email"));
	}

	@Override
	public int isBlockUser(String userId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean unblockUser(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean blockUser(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean increaseLoginFailCount(String userId) {
		// TODO Auto-generated method stub
		return false;
	}

}
