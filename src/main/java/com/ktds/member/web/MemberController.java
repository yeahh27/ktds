package com.ktds.member.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.ktds.member.service.MemberService;
import com.ktds.member.validator.MemberValidator;
import com.ktds.member.vo.MemberVO;

@Controller
public class MemberController {
	
	@Autowired
	private MemberService memberService;

	@GetMapping("/member/logout")
	public String doMemberLogoutAction(HttpSession session) {
		// Logout
		session.invalidate();	// 모든 session 날려버려
		return "redirect:/member/login";
	}
	
	@GetMapping("/member/regist")
	public String viewMemberRegistPage() {
		return "member/regist";
	}
	
	// /member/check/duplicate?email=값
	@PostMapping("/member/check/duplicate")
	@ResponseBody								//  객체를 JSON으로 
	public Map<String, Object> doCheckDuplicateEmail(@RequestParam String email) {
		Random random = new Random();
		
		Map<String, Object> result = new HashMap<>();
		result.put("status", "OK");
		result.put("duplicated", random.nextBoolean());
		return result;
	}
	
	/*@PostMapping("/member/regist")
	public ModelAndView doMemberRegistAction(@Validated({MemberValidator.Regist.class}) @ModelAttribute MemberVO memberVO, Errors errors) {
		//return this.memberService.registMember(memberVO) ? "redirect:/member/login" : "redirect:/member/regist";
		ModelAndView view = new ModelAndView("redirect:/member/login");
		
		if(errors.hasErrors()) {
			view.setViewName("member/regist");
			view.addObject("memberVO", memberVO);
			return view;
		}
		
		this.memberService.registMember(memberVO);
		
		return view;
	}*/
	
	@PostMapping("/member/regist")
	@ResponseBody
	public Map<String, Object> doMemberRegistAction(@Validated({MemberValidator.Regist.class}) @ModelAttribute MemberVO memberVO, Errors errors) {
		Map<String, Object> result = new HashMap<>();
		if(errors.hasErrors()) {
			result.put("status", false);
			return result;
		} 
		
		this.memberService.registMember(memberVO);
		result.put("status", true);
		
		return result;
	}

	@GetMapping("/member/login")
	public String viewMemberLoginPage() {
		return "/member/login";
	}
	
	/*@PostMapping("/member/login")
	public ModelAndView doMemberLoginAction(@ModelAttribute MemberVO memberVO) {
		ModelAndView view = new ModelAndView("/member/member");
		view.addObject("memberVO", this.memberService.loginMember(memberVO));
		return view;
	}*/
	
	@PostMapping("/member/login")
	public ModelAndView doMemberLoginAction(@Validated({MemberValidator.Login.class}) @ModelAttribute MemberVO memberVO, Errors errors, HttpSession session) {
		ModelAndView view = new ModelAndView("redirect:/board/list");
		
		if(errors.hasErrors()) {
			view.setViewName("member/login");
			view.addObject("memberVO", memberVO);
			return view;
		}
		
		boolean isBlockAccount = this.memberService.isBlockUser(memberVO.getEmail());
		MemberVO loginMemberVO = null;

		if (!isBlockAccount) {
			loginMemberVO = this.memberService.loginMember(memberVO);
			
			if(loginMemberVO == null) {
				this.memberService.increaseLoginFailCount(memberVO.getEmail());
			} else {
				this.memberService.unblockUser(memberVO.getEmail());
			}
		} else {
			
		}
		
		session.setAttribute("_USER_", loginMemberVO);
		
		return view;
	}
}
