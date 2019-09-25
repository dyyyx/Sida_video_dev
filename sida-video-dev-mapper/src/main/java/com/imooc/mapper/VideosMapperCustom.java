package com.imooc.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.utils.MyMapper;

public interface VideosMapperCustom extends MyMapper<Videos> {

	/**
	 * 根据条件查询视频列表
	 * 
	 * @param desc
	 * @param userId
	 * @return
	 */
	public List<VideosVO> queryAllVideos(@Param("videoDesc") String videoDesc,@Param("userId") String userId);

	
	/**
	 * 视频获赞数增加
	 * @param videoId
	 */
	public void addVideoLikeCount(String videoId);
	
	
	/**
	 * 视频获赞数减少
	 * @param videoId
	 */
	public void reduceVideoLikeCount(String videoId);


	/**
	 * 查询用户关注的其他用户的视频列表
	 * @param userId
	 * @return
	 */
	public List<VideosVO> queryMyFollowVideos(String userId);


	/**
	 * 查询用户点赞的视频列表
	 * @param userId
	 * @return
	 */
	public List<VideosVO> queryMyLikeVideos(String userId);
	
}