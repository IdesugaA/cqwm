


@RestController
@RequestMapping("/admin/report")
@Slf4j
@Api(tags="报表相关接口")

public class ReportController{

	@Resource
	private ReportService reportService

	//处理营业额统计报表请求
	
	@ApiOperation("营业额统计报表接口")
	@GetMapping("/turnoverStatistics")
	public Result<TurnoverReportVO> turnoverStatistics(@DateTimeFormat(pattern="yyyy-MM-dd")LocalDate begin,@DateTimeFormat(pattern="yyyy-MM-dd")LocalDate end){
		log.info("开始执行营业额统计报表接口：开始日期{}，结束日期{}",begin,end);

		//调用业务查询统计数据
		TurnoverReportVO turnoverReportVO = reportService.turnoverStatistics(begin,end);

		return Result.success(turnoverReportVO);

	}

	//处理用户统计报表请求
	@ApiOperation("用户统计报表接口")
	@GetMapping("/userStatistics")
	public Result<UserReportVO> userStatistics(

		@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin , 
		@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate end
	){
		log.info("开始执行用户统计报表接口：开始日期{}，结束日期{}",begin,end);

		//调用业务查询统计数据
		UserReport VO userReportVO = reportService.userStatistics(begin,end);

		return Result.success(userReportVO);
	}

	//处理订单统计报表请求
	@ApiOperation("订单统计报表接口")
	@GetMapping("/orderStatistics")
	public Result<OrderReportVO> ordersStatistics(
		@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin,
		@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate end
	){
		log.info("开始执行订单统计报单接口：开始日期{}，结束日期{}",begin,end);

		//调用业务查询统计数据
		OrderReportVO orderReportVO = reportService.ordersStatistics(begin,end);

		return Result.success(orderReportVO);

	}

	@ApiOperation("销售top10统计报表接口")
	@GetMapping("/top10")
	public Result<SalesTop10ReportVO> top10(
		@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate begin,
		@DateTimeFormat(pattern="yyyy-MM-dd") LocalDate end
	){
		log.info("开始执行销售top10统计报表接口：开始日期{}，结束日期{}",begin,end);
		//调用业务查询统计数据
		SalesTop10ReportVO salesTop10ReportVO = reportService.top10(begin,end);

		return Result.success(salesTop10ReportVO);
	}

	@ApiOperation("导出近30天运营数据报表接口")
	@GetMapping("/export")
	public void export(){
		
		log.info("开始执行导出近30天运营数据报表接口...");
		//调用业务执行导出数据
		//reportService.export(); //使用POI到处
		reportService.export2(); //使用easyExcel导出

	}

}
