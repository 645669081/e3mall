package cn.e3mall.search.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.pojo.SearchResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.search.dao.SearchDao;
import cn.e3mall.search.mapper.SearchItemMapper;
import cn.e3mall.search.service.SearchItemService;

@Service
public class SearchItemServiceImpl implements SearchItemService{
	
	@Autowired
	private SolrServer server;

	@Autowired
	private SearchItemMapper searchItemMapper;
	

	
	@Override
	public E3Result importItmes() {
		
		try{
			List<SearchItem> list = searchItemMapper.getItemList();
			
			for (SearchItem searchItem : list) {
				//向solr添加索引
				//创建文档对象
				SolrInputDocument document=new SolrInputDocument();
				
				//使用已经定义的业务域
				//向文档中添加域
				document.addField("id", searchItem.getId());
				document.addField("item_title", searchItem.getTitle());
				document.addField("item_sell_point", searchItem.getSell_point());
				document.addField("item_price", searchItem.getPrice());
				document.addField("item_image", searchItem.getImage());
				document.addField("item_category_name", searchItem.getCategory_name());
				
				server.add(document);
			}
			
			server.commit();
			
			return E3Result.ok();
		}catch(Exception e){
			e.printStackTrace();
			return E3Result.build(500, "商品导入失败");
		}
		
	}


	/**
	 * 根据商品id添加一个该商品的数据到索引库
	 */
	@Override
	public E3Result addDocument(long itemId) throws Exception {
		SearchItem item = searchItemMapper.getItemById(itemId);
		
		SolrInputDocument doc=new SolrInputDocument();
		
		doc.addField("item_category_name", item.getCategory_name());
		doc.addField("id", item.getId());
		doc.addField("item_image", item.getImage());
		doc.addField("item_price", item.getPrice());
		doc.addField("item_sell_point",item.getSell_point());
		doc.addField("item_title",item.getTitle());
		
		server.add(doc);
		
		server.commit();
	
		return E3Result.ok();
	}

	
	
	
	
}
