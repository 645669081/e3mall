package cn.e3mall.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import cn.e3mall.common.pojo.DataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.FastDFSClient;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.service.ItemService;

@Controller
public class ItemController {
	
	@Autowired
	private ItemService itemService;
	
	
	
	
	@RequestMapping("item/{id}")
	@ResponseBody
	public TbItem getItemById(@PathVariable long id){
		return itemService.getItemById(id);
	}
	
	@RequestMapping("/")
	public String showIndex() {
		return "index";
	}
	
	@RequestMapping("{page}")
	public String showPage(@PathVariable String page){
		return page;
	}
	
	
	@RequestMapping("item/list")
	@ResponseBody
	public DataGridResult getItemList(int page,int rows){
		DataGridResult result = itemService.getItemList(page, rows);
		return result;
	}
	
	
	@RequestMapping("item/save")
	@ResponseBody
	public E3Result saveItem(TbItem item,String desc){
		E3Result result=itemService.addItem(item,desc);
		return result;
	}
	
	
	
}
