package com.imooc.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.imooc.enums.VideoStatusEnum;
import com.imooc.pojo.Bgm;
import com.imooc.pojo.Comments;
import com.imooc.pojo.Videos;
import com.imooc.service.BgmService;
import com.imooc.service.VideoService;
import com.imooc.utils.FetchVideoCover;
import com.imooc.utils.IMoocJSONResult;
import com.imooc.utils.MergeVideoMp3;
import com.imooc.utils.PagedResult;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@Api(value = "视频相关业务的接口", tags = { "视频相关业务的controller" })
@RequestMapping("/video")
public class VideoController extends BasicController {
	private static Logger logger = Logger.getLogger(VideoController.class);

	@Autowired
	BgmService bgmService;

	@Autowired
	VideoService videoService;

	@ApiOperation(value = "上传视频", notes = "上传视频的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "bgmId", value = "背景音乐id", required = false, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoSeconds", value = "背景音乐播放长度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoWidth", value = "视频宽度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoHeight", value = "视频高度", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "desc", value = "视频描述", required = false, dataType = "String", paramType = "form") })
	@PostMapping(value = "/upload", headers = "content-type=multipart/form-data")
	public IMoocJSONResult upload(String userId, String bgmId, double videoSeconds, int videoWidth, int videoHeight,
			String desc, @ApiParam(value = "短视频", required = true) MultipartFile file) throws Exception {
		if (StringUtils.isBlank(userId)) {
			return IMoocJSONResult.errorMsg("用户Id不能为空！");
		}

		// String file_space = "D:/video_space_dev";
		String uploadPathDB = File.separator + userId + File.separator + "video";
		String coverPathDB = "/" + userId + "/video";

		FileOutputStream outputStream = null;
		InputStream inputStream = null;

		String finalVideoPath = "";
		try {
			if (file != null) {
				String fileName = file.getOriginalFilename();

				String arrayFileNameItem[] = fileName.split("\\.");
				String fileNamePrefix = "";
				for (int i = 0; i < arrayFileNameItem.length - 1; i++) {
					fileNamePrefix += arrayFileNameItem[i];
				}

				if (StringUtils.isNoneBlank(fileName)) {
					// 真正保存路径
					finalVideoPath = FILE_SPACE + uploadPathDB + File.separator + fileName;
					// 数据库的图片保存路径
					uploadPathDB += (File.separator + fileName);
					coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";

					File outFile = new File(finalVideoPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						outFile.getParentFile().mkdirs();
					}

					// 图片传输
					outputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
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

		// 判断bgmId是否为空
		// 如果不为空则查询bgm信息，并合并视频，生成新视频
		if (StringUtils.isNotBlank(bgmId)) {
			Bgm bgm = bgmService.queryBgmById(bgmId);

			String mp3InputPath = FILE_SPACE + bgm.getPath();

			MergeVideoMp3 tool = new MergeVideoMp3(FFMPEG_EXE);
			String videoInputPath = finalVideoPath;
			String videoOutputParh = UUID.randomUUID().toString() + ".mp4";
			uploadPathDB = File.separator + userId + File.separator + "video" + File.separator + videoOutputParh;
			finalVideoPath = FILE_SPACE + uploadPathDB;
			tool.convertor(videoInputPath, mp3InputPath, videoSeconds, finalVideoPath);
		}

		// 视频进行截图
		FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
		videoInfo.getCover(finalVideoPath, FILE_SPACE + coverPathDB);

		// 保存视频信息到数据库
		Videos video = new Videos();
		video.setAudioId(bgmId);
		video.setUserId(userId);
		video.setVideoSecond((float) videoSeconds);
		video.setVideoHeight(videoHeight);
		video.setVideoWidth(videoWidth);
		video.setVideoDesc(desc);
		video.setVideoPath(uploadPathDB);
		video.setCoverPath(coverPathDB);
		video.setStatus(VideoStatusEnum.SUCCESS.value);
		video.setCreateTime(new Date());

		String videoId = videoService.saveVideoInfo(video);

		return IMoocJSONResult.ok(videoId);
	}

	@ApiOperation(value = "上传封面", notes = "上传封面的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String", paramType = "form"),
			@ApiImplicitParam(name = "videoId", value = "视频主键id", required = true, dataType = "String", paramType = "form") })
	@PostMapping(value = "/uploadCover", headers = "content-type=multipart/form-data")
	public IMoocJSONResult uploadCover(String userId, String videoId,
			@ApiParam(value = "视频封面", required = true) MultipartFile file) throws Exception {

		if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
			return IMoocJSONResult.errorMsg("视频主键id和用户id不能为空...");
		}

		// 文件保存的命名空间
		// String fileSpace = "C:/imooc_videos_dev";
		// 保存到数据库中的相对路径
		String uploadPathDB = File.separator + userId + File.separator + "video";

		FileOutputStream fileOutputStream = null;
		InputStream inputStream = null;
		// 文件上传的最终保存路径
		String finalCoverPath = "";
		try {
			if (file != null) {

				String fileName = file.getOriginalFilename();
				if (StringUtils.isNotBlank(fileName)) {

					finalCoverPath = FILE_SPACE + uploadPathDB + File.separator + fileName;
					// 设置数据库保存的路径
					uploadPathDB += (File.separator + fileName);

					File outFile = new File(finalCoverPath);
					if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
						// 创建父文件夹
						outFile.getParentFile().mkdirs();
					}

					fileOutputStream = new FileOutputStream(outFile);
					inputStream = file.getInputStream();
					IOUtils.copy(inputStream, fileOutputStream);
				}

			} else {
				return IMoocJSONResult.errorMsg("上传出错...");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return IMoocJSONResult.errorMsg("上传出错...");
		} finally {
			if (fileOutputStream != null) {
				fileOutputStream.flush();
				fileOutputStream.close();
			}
		}

		videoService.updateVideo(videoId, uploadPathDB);

		return IMoocJSONResult.ok();
	}

	/**
	 * 
	 * @param video
	 * @param isSaveRecord
	 *            1 - 需要保存 0 - 不需要保存 ，或者为空的时候
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@ApiOperation(value = "首页视频列表", notes = "首页视频列表的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "isSaveRecord", value = "是否需要保存查询字段记录", required = false, dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "page", value = "查询页数", required = false, dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页查询的数据量", required = false, dataType = "Integer", paramType = "query") })
	@PostMapping("/showAll")
	public IMoocJSONResult showAll(@RequestBody Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {

		if (page == null) {
			page = 1;
		}
		if (pageSize == null) {
			pageSize = PAGE_SIZE;
		}

		PagedResult result = videoService.getAllVideo(video, isSaveRecord, page, pageSize);
		return IMoocJSONResult.ok(result);
	}

	@ApiOperation(value = "关注用户的视频列表", notes = "关注用户的视频列表的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "page", value = "查询页数", required = false, dataType = "Integer", paramType = "query") })
	@PostMapping("/showMyFollow")
	public IMoocJSONResult showMyFollow(String userId, Integer page) {
		if (StringUtils.isBlank(userId)) {
			return IMoocJSONResult.ok();
		}
		if (page == null) {
			page = 1;
		}

		int pageSize = 6;

		PagedResult result = videoService.getMyFollowVideo(userId, page, pageSize);
		return IMoocJSONResult.ok(result);
	}

	@ApiOperation(value = "点赞的视频列表", notes = "点赞的视频列表的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "page", value = "查询页数", required = false, dataType = "Integer", paramType = "query") })
	@PostMapping("/showMyLike")
	public IMoocJSONResult showMyLike(String userId, Integer page, Integer pageSize) {
		if (StringUtils.isBlank(userId)) {
			return IMoocJSONResult.ok();
		}
		if (page == null) {
			page = 1;
		}
		if (pageSize == null) {
			pageSize = 6;
		}

		PagedResult result = videoService.getMyLikeVideo(userId, page, pageSize);
		return IMoocJSONResult.ok(result);
	}

	@ApiOperation(value = "查询字段热力值", notes = "查询字段热力值的接口")
	@PostMapping("/hot")
	public IMoocJSONResult hot() {

		return IMoocJSONResult.ok(videoService.getHotWords());
	}

	@ApiOperation(value = "用户点赞视频", notes = "用户点赞视频的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "videoId", value = "视频Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "videoCreaterId", value = "视频发布者Id", required = true, dataType = "String", paramType = "query") })
	@PostMapping("/userLike")
	public IMoocJSONResult useLike(String userId, String videoId, String videoCreaterId) {
		videoService.userLikeVideos(userId, videoId, videoCreaterId);
		return IMoocJSONResult.ok();
	}

	@ApiOperation(value = "用户取消点赞视频", notes = "用户取消点赞视频的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "videoId", value = "视频Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "videoCreaterId", value = "视频发布者Id", required = true, dataType = "String", paramType = "query") })
	@PostMapping("/userUnLike")
	public IMoocJSONResult useUnlike(String userId, String videoId, String videoCreaterId) {
		videoService.userUnlikeVideos(userId, videoId, videoCreaterId);
		return IMoocJSONResult.ok();
	}

	@ApiOperation(value = "用户评论视频", notes = "用户评论视频的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "fatherCommentId", value = "回复的评论Id", required = false, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "toUserId", value = "评论用户Id", required = false, dataType = "String", paramType = "query") })
	@PostMapping("/saveComment")
	public IMoocJSONResult saveComment(@RequestBody Comments comments, String fatherCommentId, String toUserId) {
		comments.setFatherCommentId(fatherCommentId);
		comments.setToUserId(toUserId);
		videoService.saveComment(comments);
		return IMoocJSONResult.ok();
	}

	@ApiOperation(value = "获取评论列表", notes = "获取评论列表的接口")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "videoId", value = "视频Id", required = true, dataType = "String", paramType = "query"),
			@ApiImplicitParam(name = "page", value = "页数", required = false, dataType = "Integer", paramType = "query"),
			@ApiImplicitParam(name = "pageSize", value = "每页数据量", required = false, dataType = "Integer", paramType = "query"), })
	@PostMapping("/getVideoComments")
	public IMoocJSONResult getVideoComments(String videoId, Integer page, Integer pageSize) {
		if (StringUtils.isBlank(videoId)) {
			return IMoocJSONResult.ok();
		}

		if (page == null) {
			page = 1;
		}
		if (pageSize == null) {
			pageSize = 10;
		}
		PagedResult result = videoService.queryComments(videoId, page, pageSize);

		return IMoocJSONResult.ok(result);

	}
}
