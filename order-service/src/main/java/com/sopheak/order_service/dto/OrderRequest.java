package com.sopheak.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

public class OrderRequest {
    
    @NotBlank
    private String customerName;

    @NotBlank 
    private String productName;

    @NotNull 
    @Min (1)
    private Integer quantity;

    // Getters and Setters
    // Getter use for get/read the value of the field (mean lock like get data )
    // Setter use for set/change the value of the field(mean lock like put data )

    public String getCustomerName(){
        return customerName;
    }

    public void setCustomerName(String customerName){
        this.customerName = customerName;
    }

    public String getProductName(){
        return productName;
    }

    public void setProductName(String productName){
        this.productName = productName;
    }

    public Integer getQuantity(){
        return quantity;
    }

    public void setQuantity(Integer quantity){
        this.quantity = quantity;
    }
}
