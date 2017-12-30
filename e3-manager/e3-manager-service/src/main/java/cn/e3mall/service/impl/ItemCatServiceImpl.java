package cn.e3mall.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.TreeNode;
import cn.e3mall.mapper.TbItemCatMapper;
import cn.e3mall.pojo.TbItemCat;
import cn.e3mall.pojo.TbItemCatExample;
import cn.e3mall.pojo.TbItemCatExample.Criteria;
import cn.e3mall.service.ItemCatService;

/**
 * 商品分类service层
 * @author 64566
 *
 */
@Service
public class ItemCatServiceImpl implements ItemCatService {
	
	@Autowired
	private TbItemCatMapper itemCatMapper;
	
	
	/**
	 * 根据父菜单的id查询其下的子菜单
	 */
	@Override
	public List<TreeNode> getItemCatList(long parentId) {
		
		TbItemCatExample example=new TbItemCatExample();
		
		Criteria criteria = example.createCriteria();
		
		criteria.andParentIdEqualTo(parentId);
		
		List<TbItemCat> list = itemCatMapper.selectByExample(example);
		
		List<TreeNode> treeNodes=new ArrayList<TreeNode>();
		
		for (TbItemCat tbItemCat : list) {
			TreeNode node=new TreeNode();
			node.setId(tbItemCat.getId());
			node.setText(tbItemCat.getName());
			node.setState(tbItemCat.getIsParent()?"closed":"open");
			treeNodes.add(node);
		}
		
		return treeNodes;
	}

}
