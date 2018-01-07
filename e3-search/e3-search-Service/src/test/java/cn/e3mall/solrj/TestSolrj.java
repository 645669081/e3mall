package cn.e3mall.solrj;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrj {
	
	@Test
	public void addDocument() throws Exception {
//		第一步：把solrJ的jar包添加到工程中。
//		第二步：创建一个SolrServer，使用HttpSolrServer创建对象。
		SolrServer server=new HttpSolrServer("http://192.168.25.128:8080/solr");
//		第三步：创建一个文档对象SolrInputDocument对象。
		SolrInputDocument inputDocument=new SolrInputDocument();
//		 第四步：向文档中添加域。必须有id域，域的名称必须在schema.xml中定义。
		inputDocument.addField("id", "test001");
		inputDocument.addField("item_title", "测试商品");
		inputDocument.addField("item_price", "199");

//		第五步：把文档添加到索引库中。
		server.add(inputDocument);
//		第六步：提交。
		server.commit();

	}
	
	
	@Test
	public void deleteDocumentById() throws Exception {
//		第一步：创建一个SolrServer对象。
		SolrServer server=new HttpSolrServer("http://192.168.25.128:8080/solr");
//		第二步：调用SolrServer对象的根据id删除的方法。
		server.deleteById("hehe");
//		第三步：提交。
		server.commit();

	}
	
	
	@Test
	public void deleteDocumentByQuery() throws Exception {
		SolrServer server=new HttpSolrServer("http://192.168.25.128:8080/solr");
		server.deleteByQuery("title:change.me");
		server.commit();
	}
	
	
	
	@Test
	public void queryDocument() throws Exception {

//		第一步：创建一个SolrServer对象
		SolrServer server=new HttpSolrServer("http://192.168.25.128:8080/solr");
//		第二步：创建一个SolrQuery对象。
		SolrQuery query=new SolrQuery();
//		第三步：向SolrQuery中添加查询条件、过滤条件。。。
		query.setQuery("*:*");
//		第四步：执行查询。得到一个Response对象。
		QueryResponse response = server.query(query);
//		第五步：取查询结果。
		SolrDocumentList results = response.getResults();
		System.out.println("总记录数："+results.getNumFound());
//		第六步：遍历结果并打印。
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("id"));;
			System.out.println(solrDocument.get("content_type"));;
		}

	}
	
	
	@Test
	public void queryDocumentWithHighLighting() throws Exception {
//		第一步：创建一个SolrServer对象
		SolrServer server=new HttpSolrServer("http://192.168.25.128:8080/solr");
//		第二步：创建一个SolrQuery对象。
		SolrQuery solrQuery=new SolrQuery();
//		第三步：向SolrQuery中添加查询条件、过滤条件。。。
		solrQuery.setQuery("测试");
		//指定默认搜索域,一般为关键词复制域
		solrQuery.set("df", "item_keywords");

		
		//开启高亮显示
		solrQuery.setHighlight(true);
		
		//设置高亮显示的前后缀
		solrQuery.setHighlightSimplePre("<em>");
		solrQuery.setHighlightSimplePost("</em>");
		
		//设置高亮显示的域
		solrQuery.addHighlightField("item_title");
		

//		执行查询。得到一个Response对象。
		QueryResponse response = server.query(solrQuery);
//		取查询结果。
		SolrDocumentList results = response.getResults();
		System.out.println("总条数:"+results.getNumFound());
//		第六步：遍历结果并打印。
		for (SolrDocument solrDocument : results) {
			System.out.println(solrDocument.get("id"));
			
			
			
			//获取高亮显示的结果
			String item_title=null;
			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
			//根据浏览器页面端查询的高亮结果来拆解获取
			List<String> list = highlighting.get(solrDocument.get("id")).get("item_title");
			//判断集合中是否有高亮的结果
			if(list!=null && list.size()>0){
				item_title=list.get(0);
			}else{
				item_title=(String) solrDocument.get("item_title");
			}
			
		
			System.out.println(item_title);
			System.out.println(solrDocument.get("item_price"));
		}
	}

}
