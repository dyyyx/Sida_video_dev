package com.imooc.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.imooc.pojo.UserReport;
import com.imooc.pojo.Users;
import com.imooc.pojo.vo.PublisherVideo;
import com.imooc.pojo.vo.UsersVO;
import com.imooc.service.UserService;
import com.imooc.utils.IMoocJSONResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "用户相关业务的接口", tags = { "用户相关业务的controller" })
@RequestMapping("/user")
public class UserController {
	private static Logger logger = Logger.getLogger(UserController.class);

	@Autowired
	UserService service;

	@ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
	@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query")
	@RequestMapping("/uploadFace")
	public IMoocJSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[] file) throws IOException {
		if (StringUtils.isBlank(userId)) {
			return IMoocJSONResult.errorMsg("用户Id不能为空！");
		}
		String file_space = "D:/video_space_dev";
		String uploadPathDB = "/" + userId + "/face";

		FileOutputStream outputStream = null;
		InputStream inputStream = null;
		try {
			if (file != null && file.length > 0) {
				String fileName = file[0].getOriginalFilename();
				if (StringUtils.isNoneBlank(fileName)) {
					// 图片真正保存路径
					String finalUploadPath = file_space + uploadPathDB + "/" + fileName;
					// 数据库的图片保存路径
					uploadPathDB += ("/" + fileName);
					File outFile = new File(finalUploadPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						outFile.getParentFile().mkdirs();
					}

					// 图片传输
					outputStream = new FileOutputStream(outFile);
					inputStream = file[0].getInputStream();
					IOUtils.copy(inputStream, outputStream);

				}

			} else {
				return IMoocJSONResult.errorMsg("上传错误...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return IMoocJSONResult.errorMsg("上传错误...");
		} finally {
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
		}
		Users user = new Users();
		user.setId(userId);
		user.setFaceImage(uploadPathDB);
		// 更新用户的头像信息
		service.updateUserInfo(user);

		return IMoocJSONResult.ok(uploadPathDB);
	}

	@ApiOperation(value = "用户信息查询", notes = "用户信息查询的接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "fanId", value = "访问者Id", required = true, dataType = "String", paramType = "query")		
	})
	@PostMapping("/query")
	public IMoocJSONResult queryUserInfo(String userId,String fanId) throws IOException {
		if (StringUtils.isBlank(userId)) {
			return IMoocJSONResult.errorMsg("用户id不能为空...");
		}
		Users userInfo = service.queryUserInfo(userId);
		UsersVO usersVo = new UsersVO();
		BeanUtils.copyProperties(userInfo, usersVo);
		
		usersVo.setFollow(service.queryIfFollow(userId,fanId));
		return IMoocJSONResult.ok(usersVo);
	}

	
	@ApiOperation(value = "视频发布者信息查询", notes = "视频发布者信息查询的接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "loginUserId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "videoId", value = "视频Id", required = true, dataType = "String", paramType = "query"),	
		@ApiImplicitParam(name = "publishUserId", value = "视频发布者Id", required = true, dataType = "String", paramType = "query")		
	})
	@PostMapping("/queryPublisher")
	public IMoocJSONResult queryPublisher(String loginUserId, String videoId, String publishUserId) {
		if (StringUtils.isBlank(publishUserId)) {
			return IMoocJSONResult.errorMsg("");
		}

		// 查询视频发布者信息
		Users userInfo = service.queryUserInfo(publishUserId);
		UsersVO publisher = new UsersVO();
		BeanUtils.copyProperties(userInfo, publisher);

		// 查询用户是否点赞视频
		boolean userLikeVideos = service.isUserLikeVideos(loginUserId, videoId);

		PublisherVideo bean = new PublisherVideo();
		bean.setPublisher(publisher);
		bean.setUserLikeVideo(userLikeVideos);

		return IMoocJSONResult.ok(bean);
	}
	
	@ApiOperation(value = "关注用户", notes = "关注用户的接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "fanId", value = "粉丝Id", required = true, dataType = "String", paramType = "query"),	
	})
	@PostMapping("/beyourfans")
	public IMoocJSONResult beyourfans(String userId, String fanId) {
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
			return IMoocJSONResult.errorMsg("");
		}

		service.saveUserFanRelation(userId, fanId);

		return IMoocJSONResult.ok("关注成功...");
	}
	
	@ApiOperation(value = "取消关注用户", notes = "取消关注用户的接口")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
		@ApiImplicitParam(name = "fanId", value = "粉丝Id", required = true, dataType = "String", paramType = "query"),	
	})
	@PostMapping("/dontbeyourfans")
	public IMoocJSONResult dontbeyourfans(String userId, String fanId) {
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
			return IMoocJSONResult.errorMsg("");
		}

		service.deleteUserFanRelation(userId, fanId);

		return IMoocJSONResult.ok("取消关注成功...");
	}

	@ApiOperation(value = "举报用户", notes = "举报用户的接口")
	@PostMapping("/reportUser")
	public IMoocJSONResult reportUser(@RequestBody UserReport userReport){
		
		service.saveReport(userReport);
		
		return IMoocJSONResult.errorMsg("举报成功...");
	}
	
}
