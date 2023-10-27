package com.example.kit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.kit.data.Item;
import com.example.kit.database.ItemDatabase;

import java.math.BigDecimal;
import java.util.Date;

// TODO: Create test case for ItemDatabase
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //addDB();
        remDB();

    }

    private Item mockItem(){
        BigDecimal value = new BigDecimal(120d);
        Date date = new Date();
        return new Item("Knife", date , "Stainless Steel knife"
                ,"Sharpen on Tuesdays", value, "Vosteed", "Mini", "MINI-223023");
    }

    private ItemDatabase mockDB(){
        return new ItemDatabase();
    }

    private void addDB(){
        ItemDatabase itemDB = mockDB();
        Item item = mockItem();

        itemDB.addItem(item);
    }
    private void remDB(){
        ItemDatabase itemDB = mockDB();
        itemDB.deleteItem("JQJGZNIWiVtCZb8vaGRl");
    }


}

