package com.imooc.service;

import java.util.List;

import com.imooc.pojo.Bgm;

public interface BgmService {
	
	/**
	 * 获取背景音乐
	 * @return
	 */
	public List<Bgm> queryBgmList();
	
	/**
	 * 通过bgmId查询背景音乐
	 * @param bgmId
	 * @return
	 */
	public Bgm queryBgmById(String bgmId);

}
