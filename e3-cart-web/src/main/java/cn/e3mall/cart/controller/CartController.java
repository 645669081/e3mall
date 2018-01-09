package cn.e3mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.service.ItemService;

@Controller
public class CartController {
	
	@Value("${COOKIE_CART_NAME}")
	private String COOKIE_CART_NAME;
	
	@Value("${CART_EXPIER}")
	private Integer CART_EXPIER;
	
	@Autowired
	private ItemService itemService;
	
	@Autowired
	private CartService cartService;
	
	@RequestMapping("/cart/add/{itemId}")
	public String addCartItem(@PathVariable Long itemId,@RequestParam(defaultValue="1")Integer num,HttpServletRequest request, HttpServletResponse response){
		//判断用户是否为登录状态
		//如果登录就调用有用户id的三参方法执行
		Object object = request.getAttribute("user");
		if (object != null) {
			TbUser user = (TbUser) object;
			//取用户id
			Long userId = user.getId();
			//添加到服务端
			E3Result e3Result = cartService.addCart(userId, itemId, num);
			return "cartSuccess";
		}

		//如果登录直接把购物车信息添加到服务端
		//如果未登录保存到cookie中

		
		
		//取购物车商品列表
		List<TbItem> carItemList = getCarItemList(request);
		
		//标记变量用于判断是否执行了以前添加过的循环
		boolean flag=false;
		
		//查看以前是否添加过，添加过就累加
		for (TbItem tbitem : carItemList) {
			//此处应该是Long的包装类型，==比较的是地址值，只有其中一个为long的基本类型，才能比较数值，所以进行了转换
			if(tbitem.getId()==itemId.longValue()){
				//获取以前添加的商品数量
				Integer beforNum = tbitem.getNum();
				tbitem.setNum(beforNum+num);
				
				flag=true;
				break;
			}
		}

		//没有添加过就直接添加
		if(!flag){
			//调用查询商品信息
			TbItem tbItem = itemService.getItemById(itemId);
			
			//取一张图片
			String image = tbItem.getImage();
			if (StringUtils.isNoneBlank(image)) {
				String[] images = image.split(",");
				tbItem.setImage(images[0]);
			}

			
			//设置购买数量
			tbItem.setNum(num);
			
			carItemList.add(tbItem);
		}
	
		//把购物车列表写入cookie,并指定失效时间和编码，不然中文会乱码
		CookieUtils.setCookie(request, response, COOKIE_CART_NAME, JsonUtils.objectToJson(carItemList), CART_EXPIER, true);
		return "cartSuccess";
	}
	
	
	/**
	 * 展示购物车
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping("/cart/cart")
	public String showCartList(HttpServletRequest request,HttpServletResponse response){
		//判断用户是否登录，如果登录就先合并再展示
		Object object = request.getAttribute("user");
		
		//获取cookie中的购物车数据
		List<TbItem> carItemList = getCarItemList(request);
		
		if(object!=null){
			TbUser user=(TbUser) object;
			System.out.println("用户已经登录");
			//判断cookie中有没有购物车的数据，有就进行合并，没有就以Redis中的显示
			if(!carItemList.isEmpty()){
				E3Result result = cartService.mergeCart(user.getId(), carItemList);
				
				//删除cookie中的数据
				CookieUtils.setCookie(request, response, COOKIE_CART_NAME, "",true);
			}
			
			//获取用户列表数据
			List<TbItem> list = cartService.getCartList(user.getId());
			
			request.setAttribute("cartList", list);
			return "cart";
			
		}else{
			System.out.println("用户未登录");
		}
		
		//返回cookie中的购物车数据
		request.setAttribute("cartList", carItemList);
		
		return "cart";
	}
	
	
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	@ResponseBody
	public E3Result updateNum(@PathVariable Long itemId,@PathVariable Integer num,HttpServletRequest request,HttpServletResponse response){
		//判断是否登录，登录了就更新Redis中的购物车数据
		Object object = request.getAttribute("user");
		
		if(object!=null){
			TbUser user=(TbUser) object;
			
			cartService.updateCartItemNum(user.getId(), itemId, num);
			
			return E3Result.ok();
		}
		
		List<TbItem> carItemList = getCarItemList(request);
		
		for (TbItem tbItem : carItemList) {
			if(tbItem.getId()==itemId.longValue()){
				tbItem.setNum(num);
				break;
			}
		}
		
		CookieUtils.setCookie(request, response, COOKIE_CART_NAME, JsonUtils.objectToJson(carItemList), CART_EXPIER, true);
		
		return E3Result.ok();
				
	}
	
	
	
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteCartItem(@PathVariable Long itemId,HttpServletRequest request,HttpServletResponse response){
		//第一次登录携带购物车中的数据，要先合并才能删除，
		//但是只有展示了列表用户看到了才能删除，所以合并的功能无需调用，在展示列表时自动合并，直接删除hash和cookie中各自的就可以
		Object object = request.getAttribute("user");
		
		if(object!=null){
			TbUser user=(TbUser) object;
			
			cartService.deleteCartItem(user.getId(), itemId);
			
			return "redirect:/cart/cart.html";
		}
		
		
		List<TbItem> carItemList = getCarItemList(request);
		
		for (TbItem tbItem : carItemList) {
			if(tbItem.getId()==itemId.longValue()){
				carItemList.remove(tbItem);
				break;
			}
		}
		
		CookieUtils.setCookie(request, response, COOKIE_CART_NAME, JsonUtils.objectToJson(carItemList), CART_EXPIER, true);
		
		// 6、返回逻辑视图：在逻辑视图中做redirect跳转。
		return "redirect:/cart/cart.html";

		
	}
	
	
	/**
	 * 工具方法，用户从cookie中获取购物列表
	 * @param request
	 * @return
	 */
	private List<TbItem> getCarItemList(HttpServletRequest request){
		//使用工具类
		String cookieValue = CookieUtils.getCookieValue(request,COOKIE_CART_NAME, true);
		
		if(StringUtils.isBlank(cookieValue)){
			return new ArrayList();
		}
		
		List<TbItem> jsonToList = JsonUtils.jsonToList(cookieValue, TbItem.class);
		
		return jsonToList;
		
	}
}
