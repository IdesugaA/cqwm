



public interface CategoryService{
	//新增分类
	void save(CategoryDTO categoryDTO);


	//分页查询
	PageResult pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

	//根据id删除分类
	void deleteById(Long id);

	//修改分类
	void update(CategoryDTO categoryDTO);

	//启用，禁用分类
	void startOrStop(Integer status , Long id);

	//根据类型查询分类
	List<Category> list(Integer type);
}
