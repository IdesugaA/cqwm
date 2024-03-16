



@Mapper
public interface CategoryMapper{


	@AutoFill(OperationType.INSERT)
	@Insert("insert into category(type,name,sort,status,create_time,update_time,create_user,update_user)" + " VALUES" + " (#{type},#{name},#{sort},#{status}),#{createTime},#{updateTime},#{createUser},#{updateUser})")
	void insert(Category category);

	Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

	@Delete("delete from category where id = #{id}")
	void deleteById(Long id);

	List<Category> list(Integer type);

	@Select("select * from category where id = #{id}")
	Category findById(Long id);




}
