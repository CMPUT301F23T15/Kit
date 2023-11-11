package com.example.kit.command;

import com.example.kit.data.Item;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;

public class DeleteItemCommand extends Command {

    private final DataSource<Item> itemDataSource;
    private final Item item;

    public DeleteItemCommand(Item item) {
        itemDataSource = DataSourceManager.getInstance().getItemDataSource();
        this.item = item;
    }

    @Override
    public void execute() {
        itemDataSource.deleteDataByID(item.findID());
    }

    @Override
    public void unexecute() {
        itemDataSource.addData(item);
    }

    @Override
    public boolean isReversible() {
        return true;
    }
}
