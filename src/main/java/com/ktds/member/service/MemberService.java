package com.ktds.member.service;

import com.ktds.member.vo.MemberVO;

public interface MemberService {

	public boolean registMember(MemberVO memberVO);
	
	public MemberVO loginMember(MemberVO memberVO);
	
	public boolean isBlockUser(String email);
	public boolean unblockUser(String email);
	public boolean blockUser(String email);
	public boolean increaseLoginFailCount(String email);
}
