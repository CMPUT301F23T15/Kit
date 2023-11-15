package com.example.kit.command;

import com.example.kit.data.Tag;
import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;

import java.util.ArrayList;

public class AddTagToItemCommand extends Command {

    private final Tag tagName;
    private final String itemID;
    private final DataSource<Item, ItemSet> itemDataSource;

    public AddTagToItemCommand(Tag tag, String itemID) {
        this.tagName = tag;
        this.itemID = itemID;
        itemDataSource = DataSourceManager.getInstance().getItemDataSource();
    }

    @Override
    public void execute() {
        Item item = itemDataSource.getDataByID(itemID);
        item.addTag(tagName);
        itemDataSource.addData(item);
    }

    @Override
    public void unexecute() {}

}
