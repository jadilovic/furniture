package com.avlija.furniture.service;
import java.util.Comparator;

import com.avlija.furniture.model.Element;
 
public class ElementSifraSorter implements Comparator<Element> 
{
    @Override
    public int compare(Element o1, Element o2) {
        return o2.getName().compareToIgnoreCase(o1.getName());
    }
}