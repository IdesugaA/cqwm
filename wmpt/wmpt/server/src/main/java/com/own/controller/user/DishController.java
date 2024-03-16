





@RequestController("userDishController")
@RequestMapping("/user/dish")
@Slf4j
@Api(tags="C端-菜品浏览接口")
public class DishController{

	@Autowired
	private DishService dishService;

	@Autowired
	private RedisTemplate redisTemplate;

	@GetMapping("/list")
	@ApiOperation("根据分类id查询菜品")
	public Result<List<DishVO>> list(Long categoryId){
		//目标：实现查询菜品缓存逻辑
		//1.查询缓存是否有数据
		//定义key
		String key = "dish_" + categoryId;
		//查询缓存
		List<DishVO> list = (List<DishVO>) redisTemplate.opsForValue().get(key);
		if(CollectionUtils.isEmpty(list)){
			//如果缓存没有数据，执行数据库查询
			Dish dish = new dish();
			dish.setCategoryId(categoryId);
			dish.setStatus(StatusConstant.ENABLE);//查询起售中的菜品
			list = dishService.listWithFlavor(dish);
			//从数据库获取的数据写入缓存
			redisTemplate.opsForValue().set(key,list);
		}
		return Result.success(list);

	}

}
