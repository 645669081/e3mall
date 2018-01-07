package cn.e3mall.search.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.search.dao.SearchDao;
import cn.e3mall.search.mapper.SearchItemMapper;

@Repository
public class SearchDaoImpl implements SearchDao {
	
	@Autowired
	private SolrServer server;
	

	@Override
	public SearchResult search(SolrQuery solrQuery) throws Exception{
		SearchResult searchResult=new SearchResult();
		
		QueryResponse response = server.query(solrQuery);
		
		SolrDocumentList results = response.getResults();
		
		
		searchResult.setRecourdCount((int) results.getNumFound());
		
		List<SearchItem> list=new ArrayList<SearchItem>();
		
		Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
		//取结果并为对象赋值
		for (SolrDocument solrDocument : results) {
			SearchItem item=new SearchItem();
			
			item.setCategory_name((String) solrDocument.get("item_category_name"));
			item.setId((String) solrDocument.get("id"));
			item.setImage((String) solrDocument.get("item_image"));
			item.setPrice((long) solrDocument.get("item_price"));
			item.setSell_point((String) solrDocument.get("item_sell_point"));
			
			//取高亮结果
			String title="";
			List<String>  highlightingList= highlighting.get(solrDocument.get("id")).get("item_title");
			
			if(highlightingList!=null && highlightingList.size()>0){
				title = highlightingList.get(0);
			}else{
				title=(String) solrDocument.get("item_title");
			}
			item.setTitle(title);
			
			list.add(item);
		}
		
		searchResult.setItemList(list);

		return searchResult;
	}


	
	
	

}
