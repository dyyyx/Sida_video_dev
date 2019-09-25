package com.imooc.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.imooc.utils.RedisOperator;

@RestController
public class BasicController {

	@Autowired
	public RedisOperator redis;

	public static final String USER_REDIS_SESSION = "user-redis-session";

	// 文件保存的命名空间
	public static final String FILE_SPACE = "D:" + File.separator + "video_space_dev";

	// ffmpeg所在目录
	public static final String FFMPEG_EXE = "D:" + File.separator + "ffmpeg" + File.separator + "ffmpeg"
			+ File.separator + "bin" + File.separator + "ffmpeg.exe";

	// 每页分页的记录数
	public static final Integer PAGE_SIZE = 5;
}
