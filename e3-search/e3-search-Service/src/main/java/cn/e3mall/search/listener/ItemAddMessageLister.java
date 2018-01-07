package cn.e3mall.search.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;

import cn.e3mall.common.pojo.SearchItem;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.search.mapper.SearchItemMapper;
import cn.e3mall.search.service.SearchItemService;


public class ItemAddMessageLister implements MessageListener {
	
	@Autowired
	private SearchItemService searchItemService;
	
	@Override
	public void onMessage(Message message) {
		
		
		try {
			Long itemId=null;
			
			//获取商品id
			if(message instanceof TextMessage){
				TextMessage textMessage=(TextMessage) message;
				itemId = Long.parseLong(textMessage.getText());
			}
			
			searchItemService.addDocument(itemId);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
