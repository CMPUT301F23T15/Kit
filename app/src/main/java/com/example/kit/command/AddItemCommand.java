package com.example.kit.command;

import com.example.kit.data.source.AbstractItemDataSource;
import com.example.kit.data.source.DataSourceManager;
import com.example.kit.data.Item;

/**
 * {@link Command} that adds an {@link Item} to the datasource.
 * Reversible.
 */
public class AddItemCommand extends Command {

    private final AbstractItemDataSource dataSource;
    private final Item item;

    /**
     * Constructs a command to add the given {@link Item} to the datasource.
     * @param item Item to be added.
     */
    public AddItemCommand(Item item) {
        this.item = item;
        dataSource = DataSourceManager.getInstance().getItemDataSource();
    }

    /**
     * Adds the item to the datasource.
     */
    @Override
    public void execute() {
        dataSource.addData(item);
    }

    /**
     * Deletes the Item from the datasource.
     */
    @Override
    public void unexecute() {
        dataSource.deleteDataByID(item.findID());
    }

    /**
     * Item addition to the datasource is reversible.
     * Deletes the Item from the datasource when unexecuted.
     * @return True.
     */
    @Override
    public boolean isReversible() {
        return true;
    }
}
