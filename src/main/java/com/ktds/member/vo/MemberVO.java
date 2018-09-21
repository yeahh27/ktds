package com.ktds.member.vo;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.ktds.common.dao.support.Types;
import com.ktds.member.validator.MemberValidator;

public class MemberVO {

	@Types(alias="M_EMAIL")
	@NotEmpty(message="이메일은 필수 입력 값입니다."
			  , groups={MemberValidator.Regist.class, MemberValidator.Login.class})
	@Email(message="이메일형식으로 작성해주세요."
			, groups= {MemberValidator.Regist.class, MemberValidator.Login.class})
	private String email;
	
	@Types
	@NotEmpty(message="이름은 필수 입력 값입니다.", groups= {MemberValidator.Regist.class})
	private String name;
	
	@Types
	@NotEmpty(message="비밀번호는 필수 입력 값입니다."
			 , groups={MemberValidator.Regist.class, MemberValidator.Login.class})
	@Length(min=8, max=20, message="비밀번호는 10~20글자 사이로 입력해주세요."
			 , groups={MemberValidator.Regist.class})				// 로그인에도 length msg를 준다면 해커에게 힌트를 주는 격
	@Pattern(regexp="((?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()]).{8,})")
	private String password;		// 10글자 이상 법적 규칙
	
	@Types
	private	int point;
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}
	
	@Override
	public String toString() {
		String format = "MemberVO [Email: %s, Name: %s, Password: %s, Point: %d]";
		return String.format(format, this.email, this.name, this.password, this.point);
	}
	
}
