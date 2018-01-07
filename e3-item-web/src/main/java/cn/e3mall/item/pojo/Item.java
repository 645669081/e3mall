package cn.e3mall.item.pojo;

import cn.e3mall.pojo.TbItem;

/**
 * 此类是根据商品详情页的需要建立的pojo
 * @author 64566
 *
 */
public class Item extends TbItem{
	
	
	
	public Item() {
		
	}
	
	//根据传入的父类初始化自己的属性
	public Item(TbItem tbItem){
		this.setBarcode(tbItem.getBarcode());
		this.setCid(tbItem.getCid());
		this.setCreated(tbItem.getCreated());
		this.setId(tbItem.getId());
		this.setImage(tbItem.getImage());
		this.setNum(tbItem.getNum());
		this.setPrice(tbItem.getPrice());
		this.setSellPoint(tbItem.getSellPoint());
		this.setStatus(tbItem.getStatus());
		this.setTitle(tbItem.getTitle());
		this.setUpdated(tbItem.getUpdated());
	}
	
	//为页面提供的获取多个图片的方法
	public String[] getImages() {
		String image2 = this.getImage();
		if (image2 != null && !"".equals(image2)) {
			String[] strings = image2.split(",");
			return strings;
		}
		return null;
	}

}
