package cn.antke.bct.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzhichao on 2017/6/18.
 * 创建订单后的实体
 */
public class CreateOrderEntity extends Entity{

	@SerializedName("order_id")
	private String orderId;
	@SerializedName("order_integral")
	private String orderIntegral;//总额度
	@SerializedName("order_name")
	private String orderName;
	@SerializedName("order_total")
	private String orderTotal;//总价钱
	@SerializedName("pay_type")
	private String payType;
	@SerializedName("account_balance")
	private String accountBalance;
	@SerializedName("total")
	private String total;
	@SerializedName("user_id")
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderIntegral() {
		return orderIntegral;
	}

	public void setOrderIntegral(String orderIntegral) {
		this.orderIntegral = orderIntegral;
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public String getOrderTotal() {
		return orderTotal;
	}

	public void setOrderTotal(String orderTotal) {
		this.orderTotal = orderTotal;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getAccountBalance() {
		return accountBalance;
	}

	public void setAccountBalance(String accountBalance) {
		this.accountBalance = accountBalance;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}
}
