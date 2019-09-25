package com.imooc.mapper;

import com.imooc.pojo.Users;
import com.imooc.utils.MyMapper;

public interface UsersMapper extends MyMapper<Users> {
	
	/**
	 * 用户获赞数增加
	 * @param userId
	 */
	public void addReceiveLikeCount(String userId);
	
	/**
	 * 用户获赞数减少
	 * @param userId
	 */
	public void reduceReceiveLikeCount(String userId);
	
	/**
	 * 用户粉丝数增加
	 * @param userId
	 */
	public void addFansCount(String userId);
	
	/**
	 * 用户粉丝数减少
	 * @param userId
	 */
	public void reduceFansCount(String userId);
	
	/**
	 * 用户关注数增加
	 * @param userId
	 */
	public void addFollowersCount(String userId);
	
	/**
	 * 用户关注数减少
	 * @param userId
	 */
	public void reduceFollowersCount(String userId);
	
}