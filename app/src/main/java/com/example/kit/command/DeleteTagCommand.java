package com.example.kit.command;

import com.example.kit.data.Tag;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;

import java.util.ArrayList;

/**
 * {@link Command} that deletes a {@link Tag} from the datasource.
 * Reversible.
 */
public class DeleteTagCommand extends Command {

    private final DataSource<Tag, ArrayList<Tag>> tagDataSource;
    private final Tag tag;

    /**
     * Constructs a command to delete the given {@link Tag} from the datasource.
     * @param tag Tag to be deleted.
     */
    public DeleteTagCommand(Tag tag) {
        this.tagDataSource = DataSourceManager.getInstance().getTagDataSource();
        this.tag = tag;
    }

    /**
     * Deletes the Tag from the Tag DataSource.
     */
    @Override
    public void execute() {
        tagDataSource.deleteDataByID(tag.getName());
    }

    /**
     * Adds the Tag back to the datasource.
     */
    @Override
    public void unexecute() {
        tagDataSource.addData(tag);
    }

    /**
     * Tag deletion is reversible.
     * Adds the Tag back to the datasource when unexecuted.
     * @return True.
     */
    @Override
    public boolean isReversible() {
        return true;
    }
}
