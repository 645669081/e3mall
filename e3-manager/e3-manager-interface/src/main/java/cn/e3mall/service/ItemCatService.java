package cn.e3mall.service;

import java.util.List;

import cn.e3mall.common.TreeNode;

public interface ItemCatService {
	public List<TreeNode> getItemCatList(long parentId);
}
