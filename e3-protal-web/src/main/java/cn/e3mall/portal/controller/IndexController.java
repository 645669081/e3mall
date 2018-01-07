package cn.e3mall.portal.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.pojo.TbContent;

@Controller
public class IndexController {
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${protal.index}")
	private long protal_index;
	
	@Autowired
	private ContentService contentService;
	
	
	
	@RequestMapping("/index")
	public String showIndex(Model model){
		
		//此处传入的参数是内容的分类id，根据内容分类进行内容的查询
		List<TbContent> list=contentService.getContentList(protal_index);
		model.addAttribute("ad1List", list);
		
	
		return "index";
	}
}
