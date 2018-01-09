package cn.e3mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbItemMapper;
import cn.e3mall.pojo.TbItem;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private JedisClient jedisClient;
	
	@Autowired
	private TbItemMapper itemMapper;
	
	@Value("${CART_REDIS_KEY}")
	private String CART_REDIS_KEY;
	
	@Override
	public E3Result mergeCart(long userId, List<TbItem> itemList) {
			//遍历商品列表
			for (TbItem tbItem : itemList) {
				addCart(userId, tbItem.getId(), tbItem.getNum());
			}
			return E3Result.ok();
	}
	
	
	/**
	 * 登录后添加购物车的方法，保存到Redis中,和原来一样只是保存到Redis中
	 */
	@Override
	public E3Result addCart(long userId, long itemId, int num) {
		//判断Redis中是否有该商品信息,判断当前用户的购物车是否有该商品
		Boolean hexists = jedisClient.hexists(CART_REDIS_KEY+":"+userId, itemId+"");
		//判断是否有该商品,有就累加后存入覆盖
		if(hexists){
			//获取商品对象的json数据
			String itemInfo = jedisClient.hget(CART_REDIS_KEY+":"+userId, itemId+"");
			
			TbItem tbItem = JsonUtils.jsonToPojo(itemInfo, TbItem.class);
			
			//累加,存入
			tbItem.setNum(tbItem.getNum()+num);
			
			jedisClient.hset(CART_REDIS_KEY+":"+userId, itemId+"", JsonUtils.objectToJson(tbItem));
		
		}
		
		
		//查询该商品的信息
		TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
		
		//设置商品数量
		tbItem.setNum(num);
		
		//处理图片属性不对应，只取一张
		String image = tbItem.getImage();
		
		//取一张图片
		if (StringUtils.isNotBlank(image)) {
			tbItem.setImage(image.split(",")[0]);
		}

		
		
		//没有就添加到Redis中
		jedisClient.hset(CART_REDIS_KEY+":"+userId, itemId+"", JsonUtils.objectToJson(tbItem));
		
	
		return E3Result.ok();
	}

	
	
	/**
	 * 根据用户id获取用户的购物车列表
	 */
	@Override
	public List<TbItem> getCartList(long userId) {
		//获取该用户在Redis购物车保存的所有商品
		List<String> list = jedisClient.hvals(CART_REDIS_KEY+":"+userId);
		
		List<TbItem> itemList=new ArrayList<TbItem>();
		
		for (String string : list) {
			TbItem tbItem = JsonUtils.jsonToPojo(string, TbItem.class);
			itemList.add(tbItem);
		}
		
		return itemList;
	}

	
	/**
	 * 更新购物车的方法
	 */
	@Override
	public E3Result updateCartItemNum(long userId, long itemId, int num) {
		String itemInfo = jedisClient.hget(CART_REDIS_KEY+":"+userId, itemId+"");
		
		TbItem tbItem = JsonUtils.jsonToPojo(itemInfo, TbItem.class);
		
		tbItem.setNum(num);
		
		jedisClient.hset(CART_REDIS_KEY+":"+userId, itemId+"",JsonUtils.objectToJson(tbItem));
		return E3Result.ok();
	}

	
	/**
	 * 根据id删除用户Redis购物车中的商品
	 */
	@Override
	public E3Result deleteCartItem(long userId, long itemId) {
		jedisClient.hdel(CART_REDIS_KEY+":"+userId, itemId+"");
		return E3Result.ok();
	}

	
	
	
}
