




@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService{

	private final UserMapper userMapper;
	private final WeChatProperties weChatProperties;

	@Override
	public User login(UserLoginDTO userLoginDTO){

		//远程调用微信认证接口获取openid
		String url = "https://api.weixin.qq.com/sns/jscode2session";
		Map<String , String> map = new HashMap();
		map.put("appid",weChatProperties.getAppid());
		map.put("secret",weChatProperties.getSecret());
		map.put("js_code",userLoginDTO.getCode());
		map.put("grant_type","authorization_code");
		String json = HttpClientUtil.doGet(url,map);
		JSONObejct jsonObject = JSON.parseObject(json);
		String openid = (String) jsonObject.get("openid");
		if(openid==null){
			throw new BaseException(MessageConstant.LOGIN_FAILED);
		}
			//根据openid查询用户对象
		User user = userMapper.findByOpenId(openid);
			//没有找到对应用户对象，将openid注册到用户表中（新用户注册）
		if(user == null){
			user = new User();
			user.setOpenid(openid);
			user.setCreateTime(LocalDateTIme.now());
			userMapper.insert(user);
		}
		return user;

		}


	}



}
