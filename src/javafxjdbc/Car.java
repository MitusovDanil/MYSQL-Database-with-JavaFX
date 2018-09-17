/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javafxjdbc;

import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author sohof
 */
public class Car {
    
    private final SimpleStringProperty owner;
    private final SimpleStringProperty brand;
    private final SimpleStringProperty plateNr;
    private final SimpleStringProperty color;

    public Car(String brand,String owner,String plateNr, String color){
    
        this.brand = new SimpleStringProperty(brand);
        this.color = new SimpleStringProperty(color);
        this.plateNr = new SimpleStringProperty(plateNr);
        this.owner = new SimpleStringProperty(owner);
    }

    public String getOwner(){
        return owner.get();
    }
    public void setOwner(String s){
        owner.set(s);
    }
    public String getBrand(){
        return brand.get();
    }
    public void setBrand(String s){
        brand.set(s);
    }
    
    public String getPlateNr(){
        return plateNr.get();
    }
    public void setPlateNr(String s){
        plateNr.set(s);
    }
  
    public String getColor(){
        return color.get();
    }
    public void setColor(String s){
        color.set(s);
    }
      
       
}
