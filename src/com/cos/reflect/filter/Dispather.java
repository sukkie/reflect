package com.cos.reflect.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cos.reflect.anno.RequstMapping;
import com.cos.reflect.controller.UserController;

public class Dispather implements Filter {

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

//		System.out.println("getContextPath : " + request.getContextPath());
//		System.out.println("getRequestURI : " + request.getRequestURI());
//		System.out.println("getRequestURL : " + request.getRequestURL());

		String endPoint = request.getRequestURI().replaceAll(request.getContextPath(), "");

		System.out.println(endPoint);

//		// 1. 리플렉션 => 메소드를 런타임 시점에 찾아냄. - 함수
//		UserController userController = new UserController();
//		Method[] methods =  userController.getClass().getDeclaredMethods(); // 그 파일의 메소드
//		for (Method method : methods) {
//			if (endPoint.equals("/"+method.getName())) {
//				try {
//					method.invoke(userController);
//				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}

		// 2. 리플렉션 => 메소드를 런타임 시점에 찾아냄. - 어노테이션
		UserController userController = new UserController();
		Method[] methods = userController.getClass().getDeclaredMethods();
		
		boolean isMaching = false;
		for (Method method : methods) {
			Annotation annotaion = method.getAnnotation(RequstMapping.class);
			RequstMapping requstMapping = (RequstMapping) annotaion;
			if (requstMapping.value().equals(endPoint)) {
				try {
					isMaching = true;
					String path = null;
					Parameter[] parameters = method.getParameters();
					if (parameters.length != 0) {
//						System.out.println(parameters[0].getType());
						// JoinDto or LoginDto
						Object dtoInstance = parameters[0].getType().getDeclaredConstructor().newInstance();

						// key값을 set메소드로 변경
						setData(dtoInstance, request);

						path = (String) method.invoke(userController, dtoInstance);
					} else {
						path = (String) method.invoke(userController);
					}

					// 필터를 다시 안탐.
					RequestDispatcher rd = request.getRequestDispatcher(path);
					rd.forward(request, response);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}

		if (!isMaching) {
			response.setContentType("text/html;charset=UTF-8");
//			response.setCharacterEncoding("UTF-8");
			PrintWriter pw = response.getWriter();
			pw.println("잘못된 주소 요청입니다.");
			pw.flush();
		}
	}

	private <T> void setData(T instance, HttpServletRequest request) {
//		String username = request.getParameter("username");
//		String password = request.getParameter("password");

		Enumeration<String> keys = request.getParameterNames();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			String methodKey = keyToMehtodKey(key); // setUserName, setPassword

			Method[] methods = instance.getClass().getDeclaredMethods(); // set 2, get 2, toString = 5

			for (Method method : methods) {
				if (method.getName().equals(methodKey)) {
					try {
						method.invoke(instance, request.getParameter(key));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				}
			}
		}
	}

	private String keyToMehtodKey(String key) {
		String firstKey = "set";
		String upperKey = key.substring(0, 1).toUpperCase();
		String remainKey = key.substring(1);

		return firstKey + upperKey + remainKey;
	}

}
