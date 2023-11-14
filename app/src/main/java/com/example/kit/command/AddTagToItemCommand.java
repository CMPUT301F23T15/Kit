package com.example.kit.command;

import com.example.kit.data.Item;
import com.example.kit.data.ItemSet;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;

public class AddTagToItemCommand extends Command {

    private String tagID, itemID;

    public AddTagToItemCommand(String tagID, String itemID) {
        this.tagID = tagID;
        this.itemID = itemID;
    }

    @Override
    public void execute() {
        DataSource<Item, ItemSet> itemDataSource = DataSourceManager.getInstance().getItemDataSource();
        Item item = itemDataSource.getDataByID(itemID);
        item.addTag(tagID);
        itemDataSource.addData(item);
    }

    @Override
    public void unexecute() {}

}
