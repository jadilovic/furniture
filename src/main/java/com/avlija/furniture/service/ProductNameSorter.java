package com.avlija.furniture.service;
import java.util.Comparator;

import com.avlija.furniture.model.Product;
 
public class ProductNameSorter implements Comparator<Product> 
{
    @Override
    public int compare(Product p1, Product p2) {
        return p2.getName().compareToIgnoreCase(p1.getName());
    }
}