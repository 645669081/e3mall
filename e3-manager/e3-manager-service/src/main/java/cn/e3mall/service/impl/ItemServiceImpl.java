package cn.e3mall.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.pojo.DataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.IDUtils;
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
	
	@Override
	public TbItem getItemById(long id) {
		return itemMapper.selectByPrimaryKey(id);
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
		long id = IDUtils.genItemId();
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
		
		
		return E3Result.ok();
	}
	
	
	

}
