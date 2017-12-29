package cn.e3mall.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import cn.e3mall.common.DataGridResult;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;
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

}
