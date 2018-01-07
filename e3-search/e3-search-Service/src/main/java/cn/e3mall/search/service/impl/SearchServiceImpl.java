package cn.e3mall.search.service.impl;

import org.apache.solr.client.solrj.SolrQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.search.dao.SearchDao;
import cn.e3mall.search.service.SearchService;

@Service
public class SearchServiceImpl implements SearchService {
	
	@Autowired
	private SearchDao searchDao;
	
	
	@Override
	public SearchResult search(String keyword, Integer page, int rows) throws Exception {
		//使用关键字查询索引库并分页
		SolrQuery solrQuery=new SolrQuery();
		
		//设置搜索条件
		solrQuery.setQuery(keyword);
		
		//设置分页
		solrQuery.setStart((page-1)*rows);
		solrQuery.setRows(rows);
		
		//设置默认搜索域
		solrQuery.set("df", "item_keywords");
		
		//设置高亮
		solrQuery.setHighlight(true);
		
		//添加高亮显示的字段
		solrQuery.addHighlightField("item_title");
		
		//设置高亮显示的前后缀
		solrQuery.setHighlightSimplePre("<em>");
		solrQuery.setHighlightSimplePost("</em>");
		
		//查询
		SearchResult searchResult = searchDao.search(solrQuery);
		
		//看返回的对象中是否有页面需要返回的全部属性,设置总页数
		//计算总页数
		int recourdCount = searchResult.getRecourdCount();
		int pages = recourdCount / rows;
		if (recourdCount % rows > 0) pages++;

		searchResult.setTotalPages(pages);
		return searchResult;
	}

}
