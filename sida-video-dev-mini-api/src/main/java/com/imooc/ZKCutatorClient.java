package com.imooc;

import java.io.File;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.imooc.config.ResourceConfig;
import com.imooc.enums.BGMOperatorTypeEnum;
import com.imooc.utils.JsonUtils;

@Component
public class ZKCutatorClient {

	private CuratorFramework client = null;
	final static Logger log = LoggerFactory.getLogger(ZKCutatorClient.class);

	@Autowired
	ResourceConfig resourceConfig;

	public void init() {
		if (client != null) {
			return;
		}
		
		//重试策略
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
		
		//创建zk客户端
		client = CuratorFrameworkFactory.builder().connectString(resourceConfig.getZookeeperServer())
				.sessionTimeoutMs(10000).retryPolicy(retryPolicy).namespace("admin").build();
		
		client.start();
		
		try {
			addChildWatch("/bgm");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void addChildWatch(String nodePath) throws Exception {
		final PathChildrenCache cache = new PathChildrenCache(client, nodePath, true);
		cache.start();
		cache.getListenable().addListener(new PathChildrenCacheListener() {
			
			@Override
			public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
				if(event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)){
					//监听节点增加事件
					log.info("监听到节点增加事件");
					
					//从数据库中查询Bgm路径
					String path = event.getData().getPath();
					String operatorObjStr = new String(event.getData().getData());
					Map<String,String> map = JsonUtils.jsonToPojo(operatorObjStr, Map.class);
					String operatorType = map.get("operType");
					String songPath = map.get("path");
				
					//保存到本地的路径
					String filePath = resourceConfig.getFileSpace() + songPath;
					
					//定义下载路径
					String arrPath[] = songPath.split("\\\\");
					String finalPath = "";
					//url处理
					for(int i=0;i<arrPath.length;i++){
						if(StringUtils.isNoneBlank(arrPath[i])){
							finalPath += "/";
							finalPath += URLEncoder.encode(arrPath[i], "UTF-8");
						}
					}
					
					String bgmUrl = resourceConfig.getBgmServer() + finalPath;
					
					if(operatorType.equals(BGMOperatorTypeEnum.ADD.type)){
						URL url = new URL(bgmUrl);
						File file = new File(filePath);
						FileUtils.copyURLToFile(url, file);
						client.delete().forPath(path);
					} else if(operatorType.equals(BGMOperatorTypeEnum.DELETE.type)){
						File file = new File(filePath);
						FileUtils.forceDelete(file);
						client.delete().forPath(path);
					}
					
				}
			}
		});
		
	}

}
