package com.imooc.service.Impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.mapper.BgmMapper;
import com.imooc.pojo.Bgm;
import com.imooc.service.BgmService;

@Service
public class BgmServiceImpl implements BgmService {

	@Autowired
	BgmMapper mapper;
	
	
	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public List<Bgm> queryBgmList() {
		List<Bgm> list = mapper.selectAll();
		return list;
	}

	@Transactional(propagation = Propagation.SUPPORTS)
	@Override
	public Bgm queryBgmById(String bgmId) {
		Bgm bgm = mapper.selectByPrimaryKey(bgmId);
		return bgm;
	}

}
