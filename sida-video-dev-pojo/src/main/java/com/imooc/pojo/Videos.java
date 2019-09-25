package com.imooc.pojo;

import java.util.Date;
import javax.persistence.*;

public class Videos {
    @Id
    private String id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "audio_id")
    private String audioId;

    @Column(name = "video_desc")
    private String videoDesc;

    @Column(name = "video_path")
    private String videoPath;

    @Column(name = "video_second")
    private Float videoSecond;

    @Column(name = "video_width")
    private Integer videoWidth;

    @Column(name = "video_height")
    private Integer videoHeight;

    @Column(name = "cover_path")
    private String coverPath;

    @Column(name = "like_count")
    private Long likeCount;

    /**
     * 视频状态:1、发布成功，2、禁止播放，管理员操作
     */
    private Integer status;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return user_id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return audio_id
     */
    public String getAudioId() {
        return audioId;
    }

    /**
     * @param audioId
     */
    public void setAudioId(String audioId) {
        this.audioId = audioId;
    }

    /**
     * @return video_desc
     */
    public String getVideoDesc() {
        return videoDesc;
    }

    /**
     * @param videoDesc
     */
    public void setVideoDesc(String videoDesc) {
        this.videoDesc = videoDesc;
    }

    /**
     * @return video_path
     */
    public String getVideoPath() {
        return videoPath;
    }

    /**
     * @param videoPath
     */
    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    /**
     * @return video_second
     */
    public Float getVideoSecond() {
        return videoSecond;
    }

    /**
     * @param videoSecond
     */
    public void setVideoSecond(Float videoSecond) {
        this.videoSecond = videoSecond;
    }

    /**
     * @return video_width
     */
    public Integer getVideoWidth() {
        return videoWidth;
    }

    /**
     * @param videoWidth
     */
    public void setVideoWidth(Integer videoWidth) {
        this.videoWidth = videoWidth;
    }

    /**
     * @return video_height
     */
    public Integer getVideoHeight() {
        return videoHeight;
    }

    /**
     * @param videoHeight
     */
    public void setVideoHeight(Integer videoHeight) {
        this.videoHeight = videoHeight;
    }

    /**
     * @return cover_path
     */
    public String getCoverPath() {
        return coverPath;
    }

    /**
     * @param coverPath
     */
    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    /**
     * @return like_count
     */
    public Long getLikeCount() {
        return likeCount;
    }

    /**
     * @param likeCount
     */
    public void setLikeCount(Long likeCount) {
        this.likeCount = likeCount;
    }

    /**
     * 获取视频状态:1、发布成功，2、禁止播放，管理员操作
     *
     * @return staus - 视频状态:1、发布成功，2、禁止播放，管理员操作
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置视频状态:1、发布成功，2、禁止播放，管理员操作
     *
     * @param staus 视频状态:1、发布成功，2、禁止播放，管理员操作
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}