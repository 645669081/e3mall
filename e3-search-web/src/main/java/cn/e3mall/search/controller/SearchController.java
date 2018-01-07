package cn.e3mall.search.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.service.SearchService;

@Controller
public class SearchController {
		
	@Autowired
	private SearchService searchService;
	
	
	@Value("${PAGE_ROWS}")
	private int PAGE_ROWS;
	
	
	@RequestMapping("search")
	public String search(String keyword,@RequestParam(defaultValue="1")Integer page,Model model) throws Exception{
		//处理关键字的接收乱码问题
		keyword  = new String(keyword.getBytes("iso8859-1"),"utf-8");
		
		//测试全局异常处理器
//		int a=1/0;
		
		
		//查询并分页需要关键字，当前页和每页大小
		SearchResult result=searchService.search(keyword,page,PAGE_ROWS);
		
		//回显查询条件
		model.addAttribute("query", keyword);
		//总页数
		model.addAttribute("totalPages",result.getTotalPages());
		//当前页
		model.addAttribute("page", page);
		//商品集合信息
		model.addAttribute("itemList", result.getItemList());
		//共多少个商品，也就是查询总条数
		model.addAttribute("recourdCount",result.getRecourdCount());
		
		
		return "search";
	} 
}
