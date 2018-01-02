package cn.e3mall.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.JsonUtils;

@Controller
public class PictureController {
	
	@Value("${uploadRestlt}")
	private String url;
	
	//响应字符串，但是是json格式，保证浏览器兼容性
	@RequestMapping(value="/pic/upload",produces=MediaType.TEXT_PLAIN_VALUE+";charset=utf-8")
	@ResponseBody
	public String imageUpLoad(MultipartFile uploadFile){
		String url1=url;
		try {
			Map result=new HashMap();
			//获取原始文件名并截取后缀名
			String originalFilename = uploadFile.getOriginalFilename();
			System.out.println(originalFilename);
			String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
			
			//利用工具类读取配置文件获取追踪中心
			FastDFSClient dfsUtils=new FastDFSClient("classpath:conf/tracker.conf");
			
			url1=url1+dfsUtils.uploadFile(uploadFile.getBytes(), extName);
			result.put("url", url1);
			result.put("error", 0);
			//返回统一的json字符串用于浏览器兼容
			String json = JsonUtils.objectToJson(result);
			
			return json;
//			return result;
		} catch (Exception e) {
			Map result=new HashMap();
			result.put("message", "图片上传失败");
			result.put("error", 1);
			String json = JsonUtils.objectToJson(result);
			e.printStackTrace();
			return json;
//			return result;
		}
	}
}
