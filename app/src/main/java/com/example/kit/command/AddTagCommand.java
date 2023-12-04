package com.example.kit.command;

import com.example.kit.data.Tag;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;

import java.util.ArrayList;

/**
 * {@link Command} that adds a {@link Tag} to the datasource.
 * Reversible.
 */
public class AddTagCommand extends Command {

    private final DataSource<Tag, ArrayList<Tag>> tagDataSource;
    private final Tag tag;

    /**
     * Constructs a command to add the given {@link Tag} to the datasource.
     * @param tag Tag to be added.
     */
    public AddTagCommand(Tag tag) {
        this.tagDataSource = DataSourceManager.getInstance().getTagDataSource();
        this.tag = tag;
    }

    /**
     * Adds the tag to the datasource.
     */
    @Override
    public void execute() {
        tagDataSource.addData(tag);
    }

    /**
     * Deletes the tag from the datasource
     */
    @Override
    public void unexecute() {
        tagDataSource.deleteDataByID(tag.getName());
    }

    /**
     * Tag addition to the datasource is reversible.
     * Deletes the Tag from the datasource when unexecuted.
     * @return True.
     */
    @Override
    public boolean isReversible() {
        return true;
    }
}
