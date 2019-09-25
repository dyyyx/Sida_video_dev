package com.imooc.service.Impl;

import java.util.Date;
import java.util.List;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.mapper.CommentsMapper;
import com.imooc.mapper.CommentsMapperCustom;
import com.imooc.mapper.SearchRecordsMapper;
import com.imooc.mapper.UserLikeVideosMapper;
import com.imooc.mapper.UsersMapper;
import com.imooc.mapper.VideosMapper;
import com.imooc.mapper.VideosMapperCustom;
import com.imooc.pojo.Comments;
import com.imooc.pojo.SearchRecords;
import com.imooc.pojo.UserLikeVideos;
import com.imooc.pojo.Videos;
import com.imooc.pojo.vo.CommentsVO;
import com.imooc.pojo.vo.VideosVO;
import com.imooc.service.VideoService;
import com.imooc.utils.PagedResult;
import com.imooc.utils.TimeAgoUtils;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class VideoServiceImpl implements VideoService {

	@Autowired
	VideosMapper videoMapper;

	@Autowired
	SearchRecordsMapper searchRecordsMapper;

	@Autowired
	VideosMapperCustom videosMapperCustom;
	
	@Autowired
	UserLikeVideosMapper userLikeVideos;
	
	@Autowired
	UsersMapper usersMapper;
	
	@Autowired
	CommentsMapper commentsMapper;
	
	@Autowired
	CommentsMapperCustom commentsMapperCustom;

	@Autowired
	Sid sid;

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public String saveVideoInfo(Videos video) {
		String id = sid.nextShort();
		video.setId(id);
		videoMapper.insertSelective(video);
		return id;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateVideo(String videoId, String uploadPathDB) {
		Videos video = new Videos();
		video.setId(videoId);
		video.setCoverPath(uploadPathDB);
		videoMapper.updateByPrimaryKeySelective(video);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public PagedResult getAllVideo(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {
		// 保存搜索热词
		String videoDesc = video.getVideoDesc();
		String userId = video.getUserId();
		if (isSaveRecord != null && isSaveRecord == 1) {
			SearchRecords searchRecords = new SearchRecords();
			searchRecords.setId(sid.nextShort());
			searchRecords.setContent(videoDesc);
			searchRecordsMapper.insert(searchRecords);
		}

		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosMapperCustom.queryAllVideos(videoDesc, userId);

		PageInfo<VideosVO> pageList = new PageInfo<>(list);

		PagedResult pageResult = new PagedResult();
		pageResult.setPage(page);
		pageResult.setTotal(pageList.getPages());
		pageResult.setRows(list);
		pageResult.setRecords(pageList.getTotal());

		return pageResult;
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagedResult getMyFollowVideo(String userId, Integer page, int pageSize) {
		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);

		PageInfo<VideosVO> pageList = new PageInfo<>(list);

		PagedResult pageResult = new PagedResult();
		pageResult.setPage(page);
		pageResult.setTotal(pageList.getPages());
		pageResult.setRows(list);
		pageResult.setRecords(pageList.getTotal());

		return pageResult;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagedResult getMyLikeVideo(String userId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);

		PageInfo<VideosVO> pageList = new PageInfo<>(list);

		PagedResult pageResult = new PagedResult();
		pageResult.setPage(page);
		pageResult.setTotal(pageList.getPages());
		pageResult.setRows(list);
		pageResult.setRecords(pageList.getTotal());

		return pageResult;
	}
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<String> getHotWords() {
		return searchRecordsMapper.queryHotWords();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userLikeVideos(String userId, String videoId, String videoCreaterId) {
		UserLikeVideos ulv = new UserLikeVideos();
		ulv.setId(sid.nextShort());
		ulv.setUserId(userId);
		ulv.setVideoId(videoId);
		userLikeVideos.insert(ulv);
		
		videosMapperCustom.addVideoLikeCount(videoId);
		usersMapper.addReceiveLikeCount(videoCreaterId);
		
	}

	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void userUnlikeVideos(String userId, String videoId, String videoCreaterId) {
		Example example = new Example(UserLikeVideos.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);
		userLikeVideos.deleteByExample(example);
		
		videosMapperCustom.reduceVideoLikeCount(videoId);
		usersMapper.reduceReceiveLikeCount(videoCreaterId);
		
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveComment(Comments comments) {
		comments.setId(sid.nextShort());
		comments.setCreateTime(new Date());
		
		commentsMapper.insert(comments);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public PagedResult queryComments(String videoId, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		
		List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);
		
		for(CommentsVO vo:list){
			vo.setTimeAgoStr(TimeAgoUtils.format(vo.getCreateTime()));
		}
		
		PageInfo<CommentsVO> pageList = new PageInfo<>(list);

		PagedResult pageResult = new PagedResult();
		pageResult.setPage(page);
		pageResult.setTotal(pageList.getPages());
		pageResult.setRows(list);
		pageResult.setRecords(pageList.getTotal());

		return pageResult;
	}

	

	

}
