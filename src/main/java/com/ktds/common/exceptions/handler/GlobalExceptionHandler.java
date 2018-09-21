package com.ktds.common.exceptions.handler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.ktds.common.exceptions.PolicyViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NoHandlerFoundException.class)
	public String NoHandlerFoundExceptionHandler() {
		return "error/404";
	}
	
	// PolicyViolationException는 RuntimeException을 상속받은 것이기 때문에 instanceof로 처리할 수 있다.
	/*@ExceptionHandler(PolicyViolationException.class)
	public String PolicyViolationExceptionHandler(PolicyViolationException e) throws UnsupportedEncodingException {
		System.out.println("에러가 발생했다!!");
		return "redirect:" + e.getRedirectUri() + "?message=" + URLEncoder.encode(e.getMessage(), "UTF-8");
	}*/
	
	@ExceptionHandler(RuntimeException.class)
	public String RuntimeExceptionHandler(RuntimeException e) throws UnsupportedEncodingException {
		e.printStackTrace();
		
		if ( e instanceof PolicyViolationException) {
			PolicyViolationException pve = (PolicyViolationException) e;
			return "redirect:" + pve.getRedirectUri() + "?message=" + URLEncoder.encode(pve.getMessage(), "UTF-8");
		} else if (e instanceof EmptyResultDataAccessException) {
			EmptyResultDataAccessException erde = (EmptyResultDataAccessException) e;
			return "redirect:/member/login";
		}
		
		return "error/500";
	}
	
}
