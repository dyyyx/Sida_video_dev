package com.imooc.controller.interceptor;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.alibaba.druid.support.json.JSONUtils;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.RedisOperator;

public class MiniInterceptor extends HandlerInterceptorAdapter {
	Logger logger = Logger.getLogger(MiniInterceptor.class);

	@Autowired
	public RedisOperator redis;
	public static final String USER_REDIS_SESSION = "user-redis-session";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String userId = request.getHeader("headerUserId");
		String userToken = request.getHeader("headerUserToken");

		if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
			//String uniqueToken = redis.get(USER_REDIS_SESSION + ":" + userId);
//			if (StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)) {
//				logger.info("登录已过期，请重新登录...");
//				returnErrorResponse(new IMoocJSONResult().errorTokenMsg("登录已过期，请重新登录..."), response);
//				return false;
//			} else {
//				if (!uniqueToken.equals(userToken)) {
//					System.out.println("账号已在其它移动端登录...");
//					returnErrorResponse(new IMoocJSONResult().errorTokenMsg("账号已在其它移动端登录..."), response);
//					return false;
//				}
//			}
		} else {
			System.out.println("请登录...");
			returnErrorResponse(new IMoocJSONResult().errorTokenMsg("请登录..."),response);
			return false;
		}

		return true;

	}

	public void returnErrorResponse(IMoocJSONResult result, HttpServletResponse response) throws IOException {
		OutputStream out = null;
		try {
			response.setCharacterEncoding("utf-8");
			response.setContentType("text/json");
			out = response.getOutputStream();
			out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
			out.flush();
		} finally {
			if (out != null) {
				out.close();
			}
		}

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

	}

}
