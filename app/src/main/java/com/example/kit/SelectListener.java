package com.example.kit;

import com.example.kit.data.Item;
public interface SelectListener {
    void onItemClick(Item item);

    void onItemLongClick(Item item);
}