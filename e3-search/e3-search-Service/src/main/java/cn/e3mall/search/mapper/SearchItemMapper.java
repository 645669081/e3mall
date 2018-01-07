package cn.e3mall.search.mapper;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;

public interface SearchItemMapper {
	List<SearchItem> getItemList();

	SearchItem getItemById(long itemId);
}
