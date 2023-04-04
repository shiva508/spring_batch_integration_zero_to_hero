package com.pool.configuration.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@Component
//@Aspect
public class WeWishAspect {
	Logger logger=LoggerFactory.getLogger(WeWishAspect.class);
	
	//Controller 
	@Pointcut("execution(* com.pool.controller.*.*(..))")
	public void forWeWishController() {
		
	}

	//Service
	@Pointcut("execution(* com.pool.service.*.*(..))")
	public void forWeWishService() {
		
	}
	//Repository
	@Pointcut("execution(* com.pool.repository.*.*(..))")
	public void forWeWishRepository() {
		
	}
	
	@Pointcut("forWeWishController() || forWeWishService() ")
	public void wishCreationFlow() {
		
	}
	//dao
	
	@Before("wishCreationFlow()")
	public void beforeWishCreation(JoinPoint joinPoint) {
		String methodName=joinPoint.getSignature().toShortString();
		logger.info("BEFORE CALLING METHOD: {}", methodName);
		Object [] objects=joinPoint.getArgs();
		for(Object object:objects) {
			logger.info("arguments {}", object);
		}
	}
	
	@AfterReturning(pointcut = "wishCreationFlow()",returning = "result")
	public void afterCreateWish(JoinPoint joinPoint,Object result) {
		String methodName=joinPoint.getSignature().toShortString();
		logger.info("AFTER CALLING METHOD: {}", methodName);
		logger.info("result :{}",result);
	}
}
