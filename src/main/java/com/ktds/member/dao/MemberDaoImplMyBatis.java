package com.ktds.member.dao;

import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.ktds.member.vo.MemberVO;

@Repository
public class MemberDaoImplMyBatis extends SqlSessionDaoSupport implements MemberDao {

	private Logger logger = LoggerFactory.getLogger(MemberDaoImplMyBatis.class);
	
	@Autowired
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		logger.debug("Initiate MyBatis");
		super.setSqlSessionTemplate(sqlSessionTemplate);
	}

	@Override
	public int insertMember(MemberVO memberVO) {
		return getSqlSession().insert("MemberDao.insertMember", memberVO);
	}

	@Override
	public MemberVO selectOneMember(MemberVO memberVO) {
		return getSqlSession().selectOne("MemberDao.selectOneMember", memberVO);		// 결과가 2개이상 나오면 에러
	}

	@Override
	public int updatePoint(Map<String, Object> param) {
		return getSqlSession().update("MemberDao.updatePoint", param);
	}

	@Override
	public int isBlockUser(String email) {
		return getSqlSession().selectOne("MemberDao.isBlockUser", email);
	}

	@Override
	public boolean unblockUser(String email) {
		return getSqlSession().update("MemberDao.unblockUser", email) > 0;
	}

	@Override
	public boolean blockUser(String email) {
		return getSqlSession().update("MemberDao.blockUser", email) > 0;
	}

	@Override
	public boolean increaseLoginFailCount(String email) {
		return getSqlSession().update("MemberDao.increaseLoginFailCount", email) > 0;
	}

}
