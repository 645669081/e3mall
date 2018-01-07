package cn.e3mall.content.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.pojo.DataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.content.service.ContentService;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbContentMapper;
import cn.e3mall.pojo.TbContent;
import cn.e3mall.pojo.TbContentExample;
import cn.e3mall.pojo.TbContentExample.Criteria;

@Service
public class ContentServiceImpl implements ContentService {
	
	@Autowired
	private TbContentMapper contentMapper;
	
	@Autowired
	private JedisClient jedisClient;
	

	
	@Override
	public E3Result addContent(TbContent content) {
		Date date=new Date();
		
		content.setCreated(date);
		content.setUpdated(date);
		
		
		contentMapper.insert(content);
		
		//向缓存添加数据,数据库更新数据后通过删除当前key对应的数据下次直接从数据库来取来保持同步更新
		try{
			jedisClient.hdel("content_info", content.getCategoryId()+"");
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return E3Result.ok();
	}

	
	@Override
	public DataGridResult getContentList(long categoryId, int page, int rows) {
		//设置分页参数
		PageHelper.startPage(page, rows);
		
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		
		//构建pageinfo对象并将返回的数据放入
		List<TbContent> list = contentMapper.selectByExample(example);
		PageInfo<TbContent> info=new PageInfo<TbContent>(list);
		
		//构造返回给前端的数据
		DataGridResult result=new DataGridResult();
		result.setTotal(info.getTotal());
		result.setRows(list);
		
		return result;
	}


	@Override
	public List<TbContent> getContentList(long categoryId) {
		
		//从缓存中获取内容
		try{
			String json = jedisClient.hget("content_info",categoryId+"");
			
			if(StringUtils.isNotBlank(json)){
				List<TbContent> jsonToList = JsonUtils.jsonToList(json, TbContent.class);
				System.out.println("向缓存中拿取数据");
				return jsonToList;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		TbContentExample example=new TbContentExample();
		Criteria criteria = example.createCriteria();
		criteria.andCategoryIdEqualTo(categoryId);
		
		List<TbContent> list = contentMapper.selectByExample(example);
		
		//向缓存添加数据
		try{
			String objectToJson = JsonUtils.objectToJson(list);
			jedisClient.hset("content_info", categoryId+"", objectToJson);
			System.out.println("向缓存中添加数据");
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

}
