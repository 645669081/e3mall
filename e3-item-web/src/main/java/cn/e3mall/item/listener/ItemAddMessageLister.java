package cn.e3mall.item.listener;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.e3mall.item.pojo.Item;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.service.ItemService;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class ItemAddMessageLister implements MessageListener{
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private FreeMarkerConfigurer freeMarkerConfigurer;
	
	@Value("${HTML_OUT_PATH}")
	private String HTML_OUT_PATH;
	
	@Override
	public void onMessage(Message message) {
	
		try {
			TextMessage textMessage=(TextMessage) message;
			String itemId = textMessage.getText();
			
			if(StringUtils.isNotBlank(itemId)){
				long parseLong = Long.parseLong(itemId);
				
				//等待添加商品的事务提交
				Thread.sleep(1000);
				
				TbItem tbItem = itemService.getItemById(parseLong);
				TbItemDesc tbItemDesc = itemService.getItemDescById(parseLong);
				
				Configuration configuration = freeMarkerConfigurer.getConfiguration();
				Template template = configuration.getTemplate("item.ftl");
				
				//封装到给页面传送数据的实体类
				Item item=new Item(tbItem);
				
				//封装数据模型
				Map dataModel=new HashMap();
				dataModel.put("item", item);
				dataModel.put("itemDesc", tbItemDesc);
				
				Writer out=new FileWriter(new File(HTML_OUT_PATH+itemId+".html"));
				
				template.process(dataModel, out);
				
				out.close();
			}
		} catch (Exception e) {
		
			e.printStackTrace();
		}
	}
	
}
