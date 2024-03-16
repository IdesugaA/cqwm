





@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags="C端-分类接口")
public class CategoryController{

	@Autowired
	private CategoryService categoryService;

	@GetMapping("/list")
	@ApiOperation("查询分类")
	public Result<List<Category>> list(Integer type){
		List<Category> list = categoryService.list(type);
		return Result.success(list);
	}



}
