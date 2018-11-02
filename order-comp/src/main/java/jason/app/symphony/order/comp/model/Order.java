package jason.app.symphony.order.comp.model;

import java.util.Date;

public class Order {
	private Long id;
	private Double amount;
	private Date orderDate;
	public Order() {}
	public Order(Long id, Double amount, Date date) {
		this.id = id;
		this.amount = amount;
		this.orderDate = date;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	
	
}
