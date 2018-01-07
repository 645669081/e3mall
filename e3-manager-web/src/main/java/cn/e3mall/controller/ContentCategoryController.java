package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.pojo.TreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;

@Controller
public class ContentCategoryController {

	@Autowired
	private ContentCategoryService contentCategoryService;
	
	@RequestMapping("content/category/list")
	@ResponseBody
	public List<TreeNode> getContentCatList(@RequestParam(defaultValue="0",value="id")long parentId){
		List<TreeNode> result=contentCategoryService.getContentCatList(parentId);
		return result;
	}
	
	
	@RequestMapping("/content/category/create")
	@ResponseBody
	public E3Result addContentCategory(long parentId,String name){
		E3Result result=contentCategoryService.addContentCategory(parentId,name);
		return result;
	}
}
