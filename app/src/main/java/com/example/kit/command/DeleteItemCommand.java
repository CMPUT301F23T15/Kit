package com.example.kit.command;

import com.example.kit.data.Item;
import com.example.kit.data.source.AbstractItemDataSource;
import com.example.kit.data.source.DataSourceManager;

/**
 * {@link Command} that deletes an {@link Item} from the datasource.
 * Reversible.
 */
public class DeleteItemCommand extends Command {

    private final AbstractItemDataSource itemDataSource;
    private final Item item;

    /**
     * Constructs a command to delete the given {@link Item} from the datasource.
     * @param item Item to be deleted.
     */
    public DeleteItemCommand(Item item) {
        itemDataSource = DataSourceManager.getInstance().getItemDataSource();
        this.item = item;
    }

    /**
     * Deletes the Item from the datasource.
     */
    @Override
    public void execute() {
        itemDataSource.deleteDataByID(item.findID());
    }

    /**
     * Adds the Item back to the datasource.
     */
    @Override
    public void unexecute() {
        itemDataSource.addData(item);
    }

    /**
     * Item deletion is reversible.
     * Adds the Item back to the datasource when unexecuted.
     * @return True.
     */
    @Override
    public boolean isReversible() {
        return true;
    }
}
