


@RestController
@RequestMapping("/admin/category")
@Api(tags = "分类相关接口")
@Slf4j
public class CategoryController{
	
	@Autowired
	private CategoryService categoryService;

	//新增分类
	@PostMapping
	@ApiOperation("新增分类")

	public Result<String> save(@RequestBody CategoryDTO categoryDTO){
		log.info("新增分类：{}",categoryDTO);
		categoryService.save(categoryDTO);
		return Result.success();
	}

	//分类分页查询
	@GetMapping("/page")
	@ApiOperation("分类分页查询")
	public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
		log.info("分页查询：{}", categoryPageQueryDTO);
		PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
		return Result.success(pageResult);

	}

	@DeleteMapping
	@ApiOperation("删除分类")
	public Result<String> deleteById(Long id){
		log.info("删除分类：{}",id);
		categoryService.deleteById(id);
		return Result.success();
	}

	//启用，禁用分类
	@PutMapping("/status/{status}")
	@ApiOperation("启用禁用分类")
	public Result<String> startOrStop(@PathVariable("status") Integer status , Long id){
		categoryService.startOrStop(status,id);
		return Result.success();
	}
	//根据类型查询分类
	@GetMapping("/list")
	@ApiOperation("根据类型查询分类")
	public Result<List<Category>> list(Integer type){
		List<Category> list = categoryService.list(type);
		return Result.success(list);
	}

}