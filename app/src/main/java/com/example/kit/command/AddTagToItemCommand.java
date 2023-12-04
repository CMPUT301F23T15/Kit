package com.example.kit.command;

import com.example.kit.data.Tag;
import com.example.kit.data.Item;
import com.example.kit.data.source.AbstractItemDataSource;
import com.example.kit.data.source.DataSourceManager;

/**
 * {@link Command} to add a {@link Tag} to an {@link Item}.
 * Not reversible.
 */
public class AddTagToItemCommand extends Command {

    private final Tag tag;
    private final String itemID;
    private final AbstractItemDataSource itemDataSource;

    /**
     * Constucts a command to add the {@link Tag} to the given {@link Item}.
     * @param tag Tag to be added to the Item.
     * @param itemID Item that will receive the Tag.
     */
    public AddTagToItemCommand(Tag tag, String itemID) {
        this.tag = tag;
        this.itemID = itemID;
        itemDataSource = DataSourceManager.getInstance().getItemDataSource();
    }

    /**
     * Add the Tag to the Item.
     */
    @Override
    public void execute() {
        Item item = itemDataSource.getDataByID(itemID);
        item.addTag(tag);
        itemDataSource.addData(item);
    }

    /**
     * Not reversible, does nothing.
     */
    @Override
    public void unexecute() {}
}
