package cn.e3mall.order.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.order.pojo.OrderInfo;
import cn.e3mall.order.service.OrderService;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;

/**
 * 订单确认Controller
 * @author 64566
 *
 */
@Controller
public class OrderCartController {
	
	@Value("${COOKIE_CART_NAME}")
	private String COOKIE_CART_NAME;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Autowired
	private CartService cartService;
	
	
	@Autowired
	private OrderService orderService;
	/**
	 * 展示订单确认页面
	 * @return
	 */
	@RequestMapping("/order/order-cart")
	public String showOrderCart(HttpServletRequest request){
		//用户必须是登录状态,此操作在拦截器中完成
		//取用户id，拦截器传递，此处获取
		TbUser user=(TbUser) request.getAttribute("user");
		//根据用户信息取用户的地址列表,但是页面现在是静态的，实际应该是查询该用户关联的地址显示到页面	
		//将地址列表展示到页面
		
		List<TbItem> carItemList = getCarItemList(request);
		
		//调用服务获取购物车列表
		List<TbItem> cartList = cartService.getCartList(user.getId());

		//跳转到订单确认页面
		request.setAttribute("cartList", cartList);
		return "order-cart";
	}
	
	
	
	/**
	 * 查询用户在cookie中的购物车列表
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
	
	
	@RequestMapping(value="/order/create", method=RequestMethod.POST)
	public String createOrder(OrderInfo orderInfo, HttpServletRequest request) {
		// 1、接收表单提交的数据OrderInfo。
		// 2、补全用户信息。
		TbUser user = (TbUser) request.getAttribute("user");
		orderInfo.setUserId(user.getId());
		orderInfo.setBuyerNick(user.getUsername());
		// 3、调用Service创建订单。
		E3Result result = orderService.createOrder(orderInfo);
		//取订单号
		String orderId = result.getData().toString();
		// a)需要Service返回订单号
		request.setAttribute("orderId", orderId);
		request.setAttribute("payment", orderInfo.getPayment());
		// b)当前日期加三天。
		DateTime dateTime = new DateTime();
		dateTime = dateTime.plusDays(3);
		request.setAttribute("date", dateTime.toString("yyyy-MM-dd"));
		// 4、返回逻辑视图展示成功页面
		return "success";

	}

}
