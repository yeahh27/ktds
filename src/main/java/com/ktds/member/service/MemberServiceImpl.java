package com.ktds.member.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ktds.member.biz.MemberBiz;
import com.ktds.member.dao.MemberDao;
import com.ktds.member.vo.MemberVO;

@Service
public class MemberServiceImpl implements MemberService {
	
	@Autowired
	@Qualifier("memberDaoImplMyBatis")
	private MemberDao memberDao;
	
	@Autowired
	private MemberBiz memberBiz;

	@Override
	public boolean registMember(MemberVO memberVO) {
		return this.memberDao.insertMember(memberVO) > 0;
	}

	@Override
	public MemberVO loginMember(MemberVO memberVO) {
		
		MemberVO loginMemberVO = this.memberDao.selectOneMember(memberVO);
		
		if(loginMemberVO != null) {
			this.memberBiz.updatePoint(memberVO.getEmail(), +2);
			
			int point = loginMemberVO.getPoint();
			point += 2;
			loginMemberVO.setPoint(point);
		}
		
		return loginMemberVO;
	}

	@Override
	public boolean isBlockUser(String email) {
		return this.memberDao.isBlockUser(email) > 0;
	}

	@Override
	public boolean unblockUser(String email) {
		return this.memberDao.unblockUser(email);
	}

	@Override
	public boolean blockUser(String email) {
		return this.memberDao.blockUser(email);
	}

	@Override
	public boolean increaseLoginFailCount(String email) {
		return this.memberDao.increaseLoginFailCount(email);
	}

}
