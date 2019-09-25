package com.imooc.service;

import java.util.List;

import com.imooc.pojo.Comments;
import com.imooc.pojo.Videos;
import com.imooc.utils.PagedResult;

public interface VideoService {
	
	/**
	 * 获取背景音乐
	 * @return
	 */
	public String saveVideoInfo(Videos video);

	/**
	 * 更新视频封面到视频信息
	 * @param videoId
	 * @param uploadPathDB
	 */
	public void updateVideo(String videoId, String uploadPathDB); 

	/**
	 * 获取视频列表
	 * @param video
	 * @param isSaveRecord
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getAllVideo(Videos video,Integer isSaveRecord,Integer page,Integer pageSize);

	/**
	 * 获取热词
	 * @return
	 */
	public List<String> getHotWords();
	
	/**
	 * 用户点赞视频
	 * @param userId
	 * @param videoId
	 * @param videoCreaterId
	 */
	public void userLikeVideos(String userId,String videoId,String videoCreaterId);
	
	/**
	 * 用户取消点赞视频
	 * @param userId
	 * @param videoId
	 * @param videoCreaterId
	 */
	public void userUnlikeVideos(String userId,String videoId,String videoCreaterId);

	/**
	 * 查询用户关注的其他用户的视频
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getMyFollowVideo(String userId, Integer page, int pageSize);

	/**
	 * 查询用户点赞的视频
	 * @param userId
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public PagedResult getMyLikeVideo(String userId, Integer page, Integer pageSize);

	/**
	 * 保存视频评论
	 * @param comments
	 */
	public void saveComment(Comments comments);

	/**
	 * 查询视频评论列表
	 * @param videoId
	 * @param pageSize 
	 * @param page 
	 */
	public PagedResult queryComments(String videoId, Integer page, Integer pageSize);
}
