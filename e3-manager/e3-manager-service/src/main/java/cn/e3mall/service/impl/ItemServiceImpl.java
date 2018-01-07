package cn.e3mall.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.pojo.DataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbItemDescMapper;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbItemDesc;
import cn.e3mall.pojo.TbItemExample;
import cn.e3mall.service.ItemService;

/**
 * 商品管理类
 * @author 64566
 *
 */
@Service
public class ItemServiceImpl implements ItemService {
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemDescMapper itemDescMapper;
	
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${ITEM_INFO}")
	private String ITEM_INFO;
	
	@Value("${ITEM_EXPIRE}")
	private Integer ITEM_EXPIRE;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Resource(name="itemAddTopic")
	private Destination destination;
	
	@Override
	public TbItem getItemById(long id) {
		//先去查询缓存
		try{
			String json = jedisClient.get(ITEM_INFO+":"+id+":BASE");
			if(StringUtils.isNotBlank(json)){
				TbItem jsonToPojo = JsonUtils.jsonToPojo(json, TbItem.class);
				System.out.println("缓存中拿取了商品信息");
				return jsonToPojo;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		TbItem tbItem = itemMapper.selectByPrimaryKey(id);
		
		//将数据库查询的数据添加到缓存
		try{
			jedisClient.set(ITEM_INFO+":"+id+":BASE", JsonUtils.objectToJson(tbItem));
			//设置过期时间来解决查询热点问题
			jedisClient.expire(ITEM_INFO+":"+id+":BASE", ITEM_EXPIRE);
			System.out.println("向缓存添加了商品信息");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return tbItem;
	}

	@Override
	public DataGridResult getItemList(int page, int rows) {
		//设置分页信息
		PageHelper.startPage(page, rows);
		
		//执行查询
		TbItemExample example=new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		
		//取分页信息
		PageInfo<TbItem> pageInfo=new PageInfo<TbItem>(list);
		
		long total = pageInfo.getTotal();
		
		DataGridResult result=new DataGridResult();
		result.setTotal(total);
		result.setRows(list);
		return result;
	}

	/* (non-Javadoc)
	 * @see cn.e3mall.service.ItemService#addItem(cn.e3mall.pojo.TbItem, java.lang.String)
	 */
	@Override
	public E3Result addItem(TbItem item, String desc) {
		//设置商品id 
		final long id = IDUtils.genItemId();
		item.setId(id);
		
		
		//补全商品的其它属性
		item.setStatus((byte) 1);
		Date date=new Date();
		item.setCreated(date);
		item.setUpdated(date);
		//插入数据
		itemMapper.insert(item);
		
		
		//设置商品描述的属性
		TbItemDesc itemDesc=new TbItemDesc();
		
		itemDesc.setItemId(id);
		
		itemDesc.setCreated(date);
		itemDesc.setUpdated(date);
		
		itemDesc.setItemDesc(desc);
		
		itemDescMapper.insert(itemDesc);
		
		//添加消息队列告知添加了商品，通知其更新索引库,传递商品id即可
		jmsTemplate.send(destination, new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				TextMessage textMessage = session.createTextMessage(id+"");
				return textMessage;
			}
		});
		
		return E3Result.ok();
	}

	@Override
	public TbItemDesc getItemDescById(long itemId) {
		//先去查询缓存
		try{
			String json = jedisClient.get(ITEM_INFO+":"+itemId+":DESC");
			if(StringUtils.isNotBlank(json)){
				TbItemDesc jsonToPojo = JsonUtils.jsonToPojo(json, TbItemDesc.class);
				System.out.println("缓存中拿取了商品描述信息");
				return jsonToPojo;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	
		TbItemDesc tbItemDesc = itemDescMapper.selectByPrimaryKey(itemId);
		
		//将数据库查询的数据添加到缓存
		try{
			jedisClient.set(ITEM_INFO+":"+itemId+":DESC", JsonUtils.objectToJson(tbItemDesc));
			//设置过期时间来解决查询热点问题
			jedisClient.expire(ITEM_INFO+":"+itemId+":DESC", ITEM_EXPIRE);
			System.out.println("向缓存中加入商品描述信息");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return tbItemDesc;
	}
	
	
	

}
