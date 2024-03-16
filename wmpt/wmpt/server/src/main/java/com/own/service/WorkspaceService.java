


public interface WorkService{

	//根据时间段统计营业数据
	BusinessDataVO getBusinessData(LocalDateTime begin , LocalDateTime end);


	//查询订单管理数据
	OrderOverViewVO gerOrderOverView();

	//查询菜品总览
	DishOverViewVO getDishOverView();

	//查询套餐总览
	SetmealOverViewVO getSetmealOverView();



}
