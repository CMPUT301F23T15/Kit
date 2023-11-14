package com.example.kit.command;

import com.example.kit.data.Tag;
import com.example.kit.data.source.DataSource;
import com.example.kit.data.source.DataSourceManager;

import java.util.ArrayList;

public class DeleteTagCommand extends Command {

    private final DataSource<Tag, ArrayList<Tag>> tagDataSource;
    private final Tag tag;

    public DeleteTagCommand(Tag tag) {
        this.tagDataSource = DataSourceManager.getInstance().getTagDataSource();
        this.tag = tag;
    }

    @Override
    public void execute() {
        tagDataSource.deleteDataByID(tag.getName());
    }

    @Override
    public void unexecute() {
        tagDataSource.addData(tag);
    }

    @Override
    public boolean isReversible() {
        return true;
    }
}
