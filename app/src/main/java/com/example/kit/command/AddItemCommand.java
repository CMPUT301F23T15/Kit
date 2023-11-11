package com.example.kit.command;

import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.data.Item;

public class AddItemCommand extends Command {

    private final DataSource<Item> dataSource;
    private final Item item;

    public AddItemCommand(Item item) {
        this.item = item;
        dataSource = DataSourceManager.getInstance().getItemDataSource();
    }

    @Override
    public void execute() {
        dataSource.addData(item);
    }

    @Override
    public void unexecute() {
        dataSource.deleteDataByID(item.findID());
    }

    @Override
    public boolean isReversible() {
        return true;
    }
}
