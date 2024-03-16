





public interface ReportService{



	//营业额统计业务方法
	TurnoverReportVO turnoverStatistics(LocalDate begin , LocalDate end);

	//用户统计业务方法
	UserReportVO userStatistics(LocalDate begin, LocalDate end);

	//订单统计业务方法
	OrderReportVO ordersStatistics(LocalDate begin, LocalDate end);

	//销售top10方法
	SalesTop10ReportVO top10(LocalDate begin, LocalDate end);

	//导出近30天运营数据
	void export();

	//导出近30天运营数据，用easyexcel实现
	void export2();



}
