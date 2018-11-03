package jason.app.symphony.order.comp.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Autowired;

import jason.app.symphony.order.comp.entity.SalesOrder;
import jason.app.symphony.order.comp.model.Order;
import jason.app.symphony.order.comp.model.OrderListRequest;
import jason.app.symphony.order.comp.model.OrderListResponse;
import jason.app.symphony.order.comp.model.OrderListResponseBody;
import jason.app.symphony.order.comp.repository.SalesOrderRepository;
import jason.app.symphony.order.comp.service.OrderComponentService;


public class OrderComponentServiceImpl implements OrderComponentService {

	@Autowired
	private SalesOrderRepository orderRepo;
	
	@Override
	public void list(Exchange exchange) {
		String customerId = (String) exchange.getProperty("CUST_ID");
		OrderListRequest request = (OrderListRequest) exchange.getIn().getBody(OrderListRequest.class);
		OrderListResponse response = new OrderListResponse();
		OrderListResponseBody responseBody = new OrderListResponseBody(); 
		response.setBody(responseBody);
		List<SalesOrder> salesOrders = orderRepo.findAllByPartyId(customerId);
		List<Order> orders = new ArrayList<Order>();
		for(SalesOrder order:salesOrders) {
			orders.add(new Order(order.getId(),order.getAmount(),order.getCreateDate()));
		}
		responseBody.setOrders(orders);
		exchange.getOut().setBody(response); 
	}

}
