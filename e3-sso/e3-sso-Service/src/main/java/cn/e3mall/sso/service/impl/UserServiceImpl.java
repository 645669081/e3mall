package cn.e3mall.sso.service.impl;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import cn.e3mall.common.utils.E3Result;
import cn.e3mall.common.utils.JsonUtils;
import cn.e3mall.jedis.JedisClient;
import cn.e3mall.mapper.TbUserMapper;
import cn.e3mall.pojo.TbUser;
import cn.e3mall.pojo.TbUserExample;
import cn.e3mall.pojo.TbUserExample.Criteria;
import cn.e3mall.sso.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private TbUserMapper userMapper;
	
	@Autowired
	private JedisClient jedisClient;
	
	@Value("${USER_INFO}")
	private String USER_INFO;
	
	@Value("${SESSION_EXPIRE}")
	private int SESSION_EXPIRE;
	
	
	/**
	 * 校验注册数据是否合法
	 */
	@Override
	public E3Result checkData(String data, Integer type) {		
		TbUserExample example =new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		//根据数据类型，拼接不同的查询条件
		if(type==1){
			criteria.andUsernameEqualTo(data);
		}else if(type==2){
			criteria.andPhoneEqualTo(data);
		}else if(type==3){
			criteria.andEmailEqualTo(data);
		}else{
			return E3Result.build(400, "非法的参数");
		}
		
		List<TbUser> list = userMapper.selectByExample(example);
		
		//此处应该是没有查询到才返回true
		if(list==null || list.size()==0){
			return E3Result.ok(true);
		}
		
		return E3Result.ok(false);
	}

	
	/**
	 * 用户注册的方法
	 */
	@Override
	public E3Result createUser(TbUser user) {
		//判断是否为空
		if(StringUtils.isBlank(user.getUsername())){
			return E3Result.build(400, "用户名不能为空");
		}
		
		if(StringUtils.isBlank(user.getPassword())){
			return E3Result.build(400, "密码不能为空");
		}
		
		//先判断用户名是否重复，如果没有就无需再往下校验
		E3Result result = checkData(user.getUsername(),1);
		
		if(!(boolean) result.getData()){
			return E3Result.build(400, "用户名已存在");
		}
		
		//先对手机号进行判空，因为手机号不是强制的要求,不为空才进行校验
		if(StringUtils.isNotBlank(user.getPhone())){
			result = checkData(user.getPhone(),2);
			if(!(boolean) result.getData()){
				return E3Result.build(400, "该手机号码已经注册");
			}
		}
		
		
		if(StringUtils.isNotBlank(user.getEmail())){
			result = checkData(user.getPhone(),3);
			if(!(boolean) result.getData()){
				return E3Result.build(400, "该邮箱已经被注册");
			}
		}
		
		//补全对象属性进行注册
		Date date=new Date();
		user.setCreated(date);
		user.setUpdated(date);
		//密码用MD5加密后再存入数据库,使用spring集成的加密工具
		String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
		
		user.setPassword(md5Password);
		
		userMapper.insert(user);
		
		return E3Result.ok();
	}


	@Override
	public E3Result login(String username, String password) {
		//判空
		if(StringUtils.isBlank(username)){
			return E3Result.build(400, "用户名不能为空");
		}
		
		if(StringUtils.isBlank(password)){
			return E3Result.build(400, "密码不能为空");
		}
		
		TbUserExample example=new TbUserExample();
		Criteria criteria = example.createCriteria();
		
		//对用户名进行查询
		criteria.andUsernameEqualTo(username);
		List<TbUser> list = userMapper.selectByExample(example);
		
		if(list==null || list.size()==0){
			return E3Result.build(400, "用户名或密码不正确");
		}
		
		
		//如果查到了有该用户名的用户，然后再进行密码比对
		TbUser user = list.get(0);
		if(!user.getPassword().equals(DigestUtils.md5DigestAsHex(password.getBytes()))){
			return E3Result.build(400, "用户名或密码不正确");
		}
		
		//清除用户的密码
		user.setPassword(null);
		
		//UUID作为token，保证其不会重复
		String token=UUID.randomUUID().toString();
		
		//登录成功将用户信息存到reids,token作为键
		jedisClient.set(USER_INFO+":"+token, JsonUtils.objectToJson(user));
		
		//设置token的过期时间，模拟session的30分钟倒计时
		jedisClient.expire(USER_INFO+":"+token, SESSION_EXPIRE);
		
		return E3Result.ok(token);
	}


	
	
	@Override
	public E3Result getUserByToken(String token) {
		//判断是否为空
		if(StringUtils.isBlank(token)){
			return E3Result.build(400, "token为空");
		}
		
		//去Redis中查找是否有该token对应的信息
		String userInfo = jedisClient.get(USER_INFO+":"+token);
		
		if(StringUtils.isBlank(userInfo)){
			return E3Result.build(400, "用户登录已过期，请重新登录");
		}
		
		//更新token的作废时间
		jedisClient.expire(USER_INFO+":"+token, SESSION_EXPIRE);
		
		
		//此工具类必须将json转为对象数据才能再使用pesponse返回给浏览器json数据，直接返回拿到的json数据格式不正确
		TbUser user = JsonUtils.jsonToPojo(userInfo, TbUser.class);
		
		return E3Result.ok(user);
	}

	
	
}
