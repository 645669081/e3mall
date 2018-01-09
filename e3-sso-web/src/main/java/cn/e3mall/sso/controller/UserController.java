package cn.e3mall.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.e3mall.common.utils.CookieUtils;
import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.sso.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Value("${COOKIE_TOKEN_KEY}")
	private String COOKIE_TOKEN_KEY;
	
	@Value("${COOkIE_TOKEN_MAXAGE}")
	private int COOkIE_TOKEN_MAXAGE;
	
	@RequestMapping("/user/check/{data}/{type}")
	@ResponseBody
	public E3Result checkData(@PathVariable String data,@PathVariable Integer type){
		E3Result result = userService.checkData(data, type);
		return result;
	}
	
	
	@RequestMapping(value="/user/register",method=RequestMethod.POST)
	@ResponseBody
	public E3Result register(TbUser user){
		E3Result result=userService.createUser(user);
		return result;
	}
	
	
	@RequestMapping("/page/register")
	public String showRegister(){
		return "register";
	}
	
	
	@RequestMapping("/page/login")
	public String showLogin(String url,Model model){
		model.addAttribute("redirect", url);
		return "login";
	}
	
	
	@RequestMapping(value="/user/login",method=RequestMethod.POST)
	@ResponseBody
	public E3Result login(String username,String password,HttpServletRequest request,HttpServletResponse response){
		E3Result result=userService.login(username,password);
		
		if(result.getData()==null){
			return result;
		}
		
		String token = result.getData().toString();
		//将token写入cookie,并设置时长，查看京东关闭浏览器后并未失效
		CookieUtils.setCookie(request, response, COOKIE_TOKEN_KEY, token, COOkIE_TOKEN_MAXAGE);
		return result;
	}
	
	/**
	 * 为满足js跨域请求，改造方法
	 * @param token
	 * @return
	 */
	@RequestMapping(value="/user/token/{token}",produces=MediaType.APPLICATION_JSON_VALUE+";charset=utf-8")
	@ResponseBody
	public String getUserByToken(@PathVariable String token,String callback){
		E3Result result=userService.getUserByToken(token);
		
		//但是目前没有找到页面中要进行jsonp请求的地方
		//是否为jsonp请求,是的话拼接方法的参数
		if(StringUtils.isNotBlank(callback)){
			String strResult="("+JsonUtils.objectToJson(result)+");";
			return strResult;
		}
		
		return JsonUtils.objectToJson(result);
	}
}
