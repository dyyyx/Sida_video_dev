package com.imooc.service.Impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.mapper.UserLikeVideosMapper;
import com.imooc.mapper.UserReportMapper;
import com.imooc.mapper.UsersFansMapper;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.UserLikeVideos;
import com.imooc.pojo.UserReport;
import com.imooc.pojo.Users;
import com.imooc.pojo.UsersFans;
import com.imooc.service.UserService;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Service
public class UserServiceImpl implements UserService {
	private static Logger logger = Logger.getLogger(UserServiceImpl.class);

	@Autowired
	UsersMapper mapper;

	@Autowired
	UsersFansMapper usersFansMapper;

	@Autowired
	UserLikeVideosMapper userLikeVideosMapper;
	
	@Autowired
	UserReportMapper userReportMapper;

	@Autowired
	Sid sid;

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean queryUsernameIsExist(String username) {
		Users user = new Users();
		user.setUsername(username);
		Users result = mapper.selectOne(user);

		return result == null ? false : true;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void savaUserInfo(Users user) {
		String userId = sid.nextShort();
		user.setId(userId);
		mapper.insert(user);
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserForLogin(String username, String password) {
		Example userExample = new Example(Users.class);
		Criteria criteria = userExample.createCriteria();
		criteria.andEqualTo("username", username);
		criteria.andEqualTo("password", password);
		Users result = mapper.selectOneByExample(userExample);
		return result;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void updateUserInfo(Users user) {
		Example example = new Example(Users.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("id", user.getId());
		mapper.updateByExampleSelective(user, example);

	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Users queryUserInfo(String userId) {
		Example example = new Example(Users.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("id", userId);
		Users user = mapper.selectOneByExample(example);
		return user;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean isUserLikeVideos(String userId, String videoId) {
		if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)) {
			return false;
		}
		Example example = new Example(UserLikeVideos.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("videoId", videoId);

		List<UserLikeVideos> list = userLikeVideosMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			return true;
		}

		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveUserFanRelation(String userId, String fanId) {
		UsersFans usersFans = new UsersFans();
		usersFans.setId(sid.nextShort());
		usersFans.setUserId(userId);
		usersFans.setFanId(fanId);

		usersFansMapper.insert(usersFans);

		// 增加被关注用户粉丝数
		mapper.addFansCount(userId);
		// 增加用户关注数
		mapper.addFollowersCount(fanId);

	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void deleteUserFanRelation(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);
		usersFansMapper.deleteByExample(example);

		// 减少被关注用户粉丝数
		mapper.reduceFansCount(userId);
		// 减少用户关注数
		mapper.reduceFollowersCount(fanId);

	}

	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public boolean queryIfFollow(String userId, String fanId) {
		Example example = new Example(UsersFans.class);
		Criteria criteria = example.createCriteria();
		criteria.andEqualTo("userId", userId);
		criteria.andEqualTo("fanId", fanId);
		
		List<UsersFans> list = usersFansMapper.selectByExample(example);
		if(list!=null&&list.size()>0){
			return true;
		}
		return false;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void saveReport(UserReport userReport) {
		userReport.setId(sid.nextShort());
		userReport.setCreateDate(new Date());
		
		userReportMapper.insert(userReport);
	}
 
}
