package cn.e3mall.order.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.cart.service.CartService;
import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.pojo.TbItem;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

/**
 * 判断用户是否登录的拦截器
 * @author 64566
 *
 */
public class LoginInterceptor implements HandlerInterceptor {
	
	@Value("${COOKIE_TOKEN_KEY}")
	private String COOKIE_TOKEN_KEY;
	
	@Value("${SSO_URL}")
	private String SSO_URL;
	
	@Value("${USER_INFO}")
	private String USER_INFO;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	
	@Value("${COOKIE_CART_NAME}")
	private String COOKIE_CART_NAME;
	
	
	@Autowired
	private JedisClient jedisClient;
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e)
			throws Exception {
		// 在ModelAndView返回之后，可以处理异常

	}
	
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mv)
			throws Exception {
		//在handler执行之后，modelAndview返回之前，可以对ModelAndView做一些处理

	}
	
	/**
	 * 执行handler前被调用，返回true放行，返回false拦截
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//获取cookie中的token
		String token = CookieUtils.getCookieValue(request, COOKIE_TOKEN_KEY);
		//判断是否为空，如果为空调用sso服务进行登录，并携带要访问的页面地址
		//获取当前请求的URL
		String url = request.getRequestURL().toString();
		if(StringUtils.isBlank(token)){
			response.sendRedirect(SSO_URL+"page/login?url="+url);
			return false;
		}
		//不为空调用sso服务查询该token对应的用户信息再Redis中是否存在
		E3Result result = userService.getUserByToken(token);
		
		//根据返回的状态码来判断该用户是否还在Redis中，如果如存在就调用sso服务进行登录，并携带要访问的页面地址
		if(result.getStatus()!=200){
			response.sendRedirect(SSO_URL+"page/login?url="+url);
			return false;
		}
		
		//将用户信息放到request中传递，供登录方法获取使用
		TbUser user=(TbUser) result.getData();
		request.setAttribute("user", user);
		
		//走到这代表用户已经登录过，所以需要立即将cookie购物车中的商品数据转移上Redis中，并清除cookie购物车的数据
		//获取cookie中的购物车列表，然后判断是否为空
		String cart = CookieUtils.getCookieValue(request,COOKIE_CART_NAME,true);
		
		//不为空就将购物车转移到Redis，并删除该cookie
		if(StringUtils.isNotBlank(cart)){
			
			//调用购物车服务合并购物车，原本应该由购物车服务层提供服务，但是现在没有创建工程，使用内部直接实现
			//使用哈希在Redis中存储，键为用户id，filed为商品id，value为商品信息，避免了string下一次加载大量的商品数据
			cartService.mergeCart(user.getId(), JsonUtils.jsonToList(cart, TbItem.class));

			//删除cookie中的购物车数据
			CookieUtils.setCookie(request, response, COOKIE_CART_NAME, "");
	
		}
		
		
		//不为空则放行
		return true;
	}

}
