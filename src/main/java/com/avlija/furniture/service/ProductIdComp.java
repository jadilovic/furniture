package com.avlija.furniture.service;

import java.util.Comparator;

import com.avlija.furniture.model.Product;

public class ProductIdComp implements Comparator<Product>{
 
    @Override
    public int compare(Product e1, Product e2) {
        if(e1.getId() > e2.getId()){
            return 1;
        } else {
            return -1;
        }
    }
}