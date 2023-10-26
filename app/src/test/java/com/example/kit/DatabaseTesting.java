package com.example.kit;

import com.example.kit.data.Item;
import com.example.kit.database.ItemDatabase;
import com.google.firebase.firestore.CollectionReference;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

public class DatabaseTesting {
    // TODO: You cannot create a mock DB easily, I will look into it later but will probably test it in Main Activity

    private Item mockItem(){
        BigDecimal value = new BigDecimal(120d);
        Date date = new Date();
        return new Item("Knife", date , "Stainless Steel knife"
                ,"Sharpen on Tuesdays", value, "Vosteed", "Mini", "MINI-223023");
    }

    private ItemDatabase mockDB(){
        return new ItemDatabase();
    }


//    @Test
//    public void DatabaseTestAdd(){
//        ItemDatabase itemDB = mockDB();
//        Item item = mockItem();
//
//        itemDB.addItem(item);
//    }


}
