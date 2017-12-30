package cn.e3mall.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.TreeNode;
import cn.e3mall.service.ItemCatService;

/**
 * 商品分类的control
 * @author 64566
 *
 */
@Controller
public class ItemCatController {
	
	@Autowired
	private ItemCatService itemCatService;
	
	
	@RequestMapping("item/cat/list")
	@ResponseBody
	public List<TreeNode> getItemCatList(@RequestParam(defaultValue="0",value="id")long parentId){
		List<TreeNode> list = itemCatService.getItemCatList(parentId);
		return list;
	}
}
