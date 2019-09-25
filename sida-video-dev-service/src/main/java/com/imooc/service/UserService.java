package com.imooc.service;

import com.imooc.pojo.UserReport;
import com.imooc.pojo.Users;

public interface UserService {

	/**
	 * 查询用户名是否已经存在
	 * @param username
	 * @return
	 */
	public boolean queryUsernameIsExist(String username);
	
	
	/**
	 * 保存用户信息
	 * @param user
	 */
	public void savaUserInfo(Users user);
	
	/**
	 * 查找用户信息
	 * @param user
	 * @return
	 */
	public Users queryUserForLogin(String username, String password);
	
	/**
	 * 更新用户信息
	 * @param user
	 */
	public void updateUserInfo(Users user);
	
	/**
	 * 查询用户信息
	 * @param userId
	 * @return
	 */
	public Users queryUserInfo(String userId);
	
	/**
	 * 查询用户对视频是否进行点赞操作
	 * @param userId
	 * @param videoId
	 * @return
	 */
	public boolean isUserLikeVideos(String userId,String videoId);
	
	/**
	 * 关注用户
	 * @param userId
	 * @param fanId
	 */
	public void saveUserFanRelation(String userId,String fanId);
	
	/**
	 * 取消关注用户
	 * @param userId
	 * @param fanId
	 */
	public void deleteUserFanRelation(String userId,String fanId);


	/**
	 * 查询访问者是否关注用户
	 * @param userId
	 * @param fanId
	 * @return
	 */
	public boolean queryIfFollow(String userId, String fanId);

	/**
	 * 保存举报信息
	 * @param userReport
	 */
	public void saveReport(UserReport userReport);
}
