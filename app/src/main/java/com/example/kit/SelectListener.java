package com.example.kit;

import android.view.View;

import com.example.kit.data.Item;

public interface SelectListener {
    void onItemClick(Item item);

    void onItemLongClick();

    void setItemChecked(View view);

    void onAddTagClick();
}
