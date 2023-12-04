package com.example.kit.command;

import com.example.kit.data.Item;
import com.example.kit.data.Tag;
import com.example.kit.data.source.AbstractItemDataSource;
import com.example.kit.data.source.DataSourceManager;

public class RemoveTagFromItemCommand extends Command {

    private final Tag tag;
    private final String itemID;
    private final AbstractItemDataSource itemDataSource;

    public RemoveTagFromItemCommand(Tag tag, String itemID) {
        this.tag = tag;
        this.itemID = itemID;
        this.itemDataSource = DataSourceManager.getInstance().getItemDataSource();
    }

    @Override
    public void execute() {
        Item item = itemDataSource.getDataByID(itemID);
        item.removeTag(tag);
        itemDataSource.addData(item);
    }

    @Override
    public void unexecute() {

    }
}
