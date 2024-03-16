public interface OrderService{



	OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO);


	OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

	void paySuccess(String outTradeNo);

	PageResult page(OrdersPageQueryDTO ordersPageQueryDTO);


	Orders findById(Long id);


	OrderVO details(Long id);

	void userCancelById(Long id) throws Exception;

	void repetition(Long id);



}
