package cn.e3mall.solrj;


import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class TestSolrCloud {
	
	@Test
	public void testSolrCloudAddDocument() throws Exception {
//		第一步：把solrJ相关的jar包添加到工程中。
//		第二步：创建一个SolrServer对象，需要使用CloudSolrServer子类。构造方法的参数是zookeeper的地址列表。
		CloudSolrServer cloudSolrServer=new CloudSolrServer("192.168.25.128:2183,192.168.25.128:2184,192.168.25.128:2185");
//		第三步：需要设置DefaultCollection属性。
		cloudSolrServer.setDefaultCollection("collection2");
//		第四步：创建一SolrInputDocument对象。
		SolrInputDocument doc=new SolrInputDocument();
//		第五步：向文档对象中添加域
		doc.addField("id", "test001");
		doc.addField("item_title", "测试商品名称");
		doc.addField("item_price", 100);
//		第六步：把文档对象写入索引库。
		cloudSolrServer.add(doc);
//		第七步：提交。
		cloudSolrServer.commit();

	}
}
