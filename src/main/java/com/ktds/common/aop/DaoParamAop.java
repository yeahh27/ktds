package com.ktds.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DaoParamAop {
	
	private Logger logger = LoggerFactory.getLogger(DaoParamAop.class);
	
	// around
	public Object getParam(ProceedingJoinPoint pjp) {
		
		String classAndMethod = pjp.getSignature().toShortString();
		
		// before
		// Parameter 가져오기
		logger.debug("Before");
		Object[] paramArray = pjp.getArgs();
		
		for( Object param : paramArray) {
			logger.debug(classAndMethod + " = " + param.toString());
		}
		
		// 기존의 메소드 실행
		Object result = null;
		
		try {
			// before
			logger.debug("Before");
			result = pjp.proceed();
			// after-returning
			logger.debug(classAndMethod + " = Result : " + result.toString());
		} catch (Throwable e) {		// Throwable : 에러 최상단 계층 (밑에 exception, 그 밑에 RuntimeException...)
			// after-throwing
			logger.debug("afterThrowing");
			logger.debug(classAndMethod + " = Exception : " + e.getCause().toString() + " , " + e.getMessage());
			throw new RuntimeException(e.getMessage(), e);		// GlobalExceptionHandler로 가랏
		} /*finally{ 
			// after
			logger.debug("after");
		}

		// after-returning
		logger.debug("afterReturning");*/
		return result;
	}

}
