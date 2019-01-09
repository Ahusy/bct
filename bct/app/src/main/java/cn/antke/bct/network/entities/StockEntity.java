package cn.antke.bct.network.entities;

import com.google.gson.annotations.SerializedName;

/**
 * Created by WangFan on 2017/11/7.
 */

public class StockEntity extends Entity {

    @SerializedName("stock_quantity")
    private String stockQuantity;//库存
    @SerializedName("selling_price")
    private String sellingPrice;//价格
    @SerializedName("selling_integral")
    private String sellingIntegral;//额度


    public String getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(String stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(String sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getSellingIntegral() {
        return sellingIntegral;
    }

    public void setSellingIntegral(String sellingIntegral) {
        this.sellingIntegral = sellingIntegral;
    }
}
