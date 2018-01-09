package cn.e3mall.cart.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

public class LoginInterceptor implements HandlerInterceptor {

	@Value("${COOKIE_TOKEN_KEY}")
	private String COOKIE_TOKEN_KEY;
	
	@Autowired
	private UserService userService;
	
	
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		// TODO Auto-generated method stub

	}
	
	/**
	 * 执行前进行拦截
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//从cookie中获取token判断登录状态
		String token = CookieUtils.getCookieValue(request, COOKIE_TOKEN_KEY);
		
		//如果未空直接放行
		if(StringUtils.isBlank(token)){
			return true;
		}
		
		//不为空查询注入user用户的信息进行传递
		E3Result result = userService.getUserByToken(token);
		
		if(result.getStatus()!=200){
			return true;
		}
		
		//走到此处通过了校验，注入用户信息
		TbUser user=(TbUser) result.getData();
		
		request.setAttribute("user", user);
		
		return true;
	}

}
