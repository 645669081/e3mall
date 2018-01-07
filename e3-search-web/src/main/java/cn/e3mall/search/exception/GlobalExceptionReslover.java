package cn.e3mall.search.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

public class GlobalExceptionReslover implements HandlerExceptionResolver {
	
	//创建slf4j的日志打印对象
	private static final Logger logger=LoggerFactory.getLogger(GlobalExceptionReslover.class);
	
	
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
			Exception e) {
		logger.info("进入全局异常处理器");
		logger.debug("handler的类型是:"+handler.getClass());
//		控制台打印异常
		e.printStackTrace();
//		向日志文件写入异常信息
		logger.error("系统发生异常",e);
//		发邮件,使用邮件发送的工具类进行发送
		
//		发短信，使用第三方提供的接口调用进行发送
//		展示错误页面
		ModelAndView mv=new ModelAndView();
		//展示一个友好的给用户的提示
//		mv.addObject("message", "您的网络有问题，请稍后重试");
		mv.setViewName("error/exception");
		return mv;
	}

}
