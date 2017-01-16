/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fxtestapplication;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author bluroe
 */
public class SaleItem {

    private final SimpleStringProperty code;
    private final SimpleIntegerProperty qty;
    private final SimpleDoubleProperty price;
    
    public SaleItem() {
        this("", 0, 0);
    }
    
    public SaleItem(String code, int qty, double price) {
        this.code = new SimpleStringProperty(code);
        this.qty = new SimpleIntegerProperty(qty);
        this.price = new SimpleDoubleProperty(price);
    }
    
    public SimpleStringProperty codeProperty() {
        return code;
    }
    
    public SimpleDoubleProperty priceProperty() {
        return price;
    }
    
    public SimpleIntegerProperty qtyProperty() {
        return qty;
    }
    
    public String getCode() {
        return code.get();
    }
    
    public void setCode(String code) {
        this.code.set(code);
    }
    
    public int getQty() {
        return qty.get();
    }
    
    public void setQty(int qty) {
        this.qty.set(qty);
    }
    
    public double getPrice() {
        return price.get();
    }
    
    public void setPrice(double price) {
        this.price.set(price);
    }
    
}
