package cn.e3mall.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.e3mall.common.pojo.TreeNode;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.content.service.ContentCategoryService;
import cn.e3mall.mapper.TbContentCategoryMapper;
import cn.e3mall.pojo.TbContentCategory;
import cn.e3mall.pojo.TbContentCategoryExample;
import cn.e3mall.pojo.TbContentCategoryExample.Criteria;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	
	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	
	@Override
	public List<TreeNode> getContentCatList(long parentId) {
		// 1、创建一个Example对象
		TbContentCategoryExample example = new TbContentCategoryExample();
		// 2、设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		// 3、执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		// 4、把内容分类列表转换成节点列表
		List<TreeNode> treeNodes = new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			TreeNode node = new TreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			//添加到节点列表
			treeNodes.add(node);
		}
		// 5、返回节点列表
		return treeNodes;
	}

	
	
	@Override
	public E3Result addContentCategory(long parentId, String name) {
		TbContentCategory cat=new TbContentCategory();
		
		Date date=new Date();
		cat.setCreated(date);
		cat.setIsParent(false);
		cat.setName(name);
		cat.setParentId(parentId);
		cat.setSortOrder(1);
		cat.setStatus(1);
		cat.setUpdated(date);
		
		contentCategoryMapper.insert(cat);
		
		//如果当前节点挂接的是一个子节点的话就更新为父节点
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if(!parent.getIsParent()){
			parent.setIsParent(true);
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		
		E3Result result=new E3Result(cat);
		
		return result;
	}


}
