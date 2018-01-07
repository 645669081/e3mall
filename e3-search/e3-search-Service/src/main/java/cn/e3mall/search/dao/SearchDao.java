package cn.e3mall.search.dao;

import org.apache.solr.client.solrj.SolrQuery;

import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.common.utils.E3Result;

public interface SearchDao {
	public SearchResult search(SolrQuery solrQuery) throws Exception;
	
}
