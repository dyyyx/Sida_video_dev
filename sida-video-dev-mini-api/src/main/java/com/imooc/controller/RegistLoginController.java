package com.imooc.controller;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.imooc.pojo.Users;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MD5Utils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "用户登录注册的接口", tags = { "注册和登录的controller" })
public class RegistLoginController extends BasicController {

	private static Logger logger = Logger.getLogger(RegistLoginController.class);

	@Autowired
	UserService service;

	@ApiOperation(value = "用户注册", notes = "用户注册的接口")
	@PostMapping("/regist")
	public IMoocJSONResult regist(@RequestBody Users user) throws Exception {
		logger.info("发起注册请求: username-->" + user.getUsername() + ",password-->" + user.getPassword());
		// 判断用户名和密码是否为空
		if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
			return IMoocJSONResult.errorMsg("用户名和密码不能为空！");
		}

		// 查询用户名是否已经存在
		boolean isExist = service.queryUsernameIsExist(user.getUsername());

		// 保存用户注册信息
		if (!isExist) {
			user.setFansCounts(0);
			user.setFollowCounts(0);
			user.setReceiveLikeCounts(0);
			user.setNickname(user.getUsername());
			user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
			service.savaUserInfo(user);
			logger.info("注册成功！");
		} else {
			return IMoocJSONResult.errorMsg("用户名已存在，请更换用户名后再试！");
		}
		//UsersVO usersVO = this.setUserSessionToken(user);
		
		return IMoocJSONResult.ok(user);
		//return IMoocJSONResult.ok(usersVO);

	}

//	public UsersVO setUserSessionToken(Users user) {
//		String userToken = UUID.randomUUID().toString();
//		redis.set(USER_REDIS_SESSION + ":" + user.getId(), userToken, 1000 * 60 * 30);
//		UsersVO userVo = new UsersVO();
//		BeanUtils.copyProperties(user, userVo);
//		userVo.setUserToken(userToken);
//		return userVo;
//	}

	@ApiOperation(value = "用户登录", notes = "用户登录的接口")
	@PostMapping("/login")
	public IMoocJSONResult login(@RequestBody Users user) throws Exception {
		String username = user.getUsername();
		String password = user.getPassword();
		// 判断登录时用户名密码是否为空
		if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
			return IMoocJSONResult.errorMsg("用户名和密码不能为空！");
		}
		// 查询用户
		Users result = service.queryUserForLogin(username, MD5Utils.getMD5Str(password));
		if (result == null) {
			return IMoocJSONResult.errorMsg("用户名密码错误！");
		} else {
			//UsersVO usersVO = this.setUserSessionToken(result);
			logger.info(result.getId() + "发起登录请求");
			logger.info("登录成功");
			return IMoocJSONResult.ok(result);
//			return IMoocJSONResult.ok(usersVO);
		}

	}

	@ApiOperation(value = "用户注销", notes = "用户注销的接口")
	@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query")
	@PostMapping("/logout")
	public IMoocJSONResult logout(String userId) throws Exception {
//		redis.del(USER_REDIS_SESSION + ":" + userId);
		logger.info(userId + "注销成功");
		return IMoocJSONResult.ok();

	}
}
