package com.example.kit;

import android.view.View;

import com.example.kit.data.Item;
import com.example.kit.database.ItemViewHolder;

public interface SelectListener {
    void onItemClick(Item item);
    void onItemLongClick();
    void onAddTagClick();
}
