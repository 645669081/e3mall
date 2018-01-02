package cn.e3mall.service;

import java.util.Map;


import cn.e3mall.common.pojo.DataGridResult;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbItem;

public interface ItemService {
	public TbItem getItemById(long id);
	public DataGridResult getItemList(int page,int rows);
	public E3Result addItem(TbItem item, String desc);
}
