package tv.quaint.multidb.utils.objects;

import org.bukkit.entity.Player;
import tv.quaint.multidb.utils.DataSource;

public class Syncable {
    public String identifier;
    public boolean isString;
    public PullAndPushInfo pullFrom;
    public PullAndPushInfo pushTo;

    public Syncable(String identifier, boolean isString, PullAndPushInfo pullFrom, PullAndPushInfo pushTo) {
        this.identifier = identifier;
        this.isString = isString;
        this.pullFrom = pullFrom;
        this.pushTo = pushTo;
    }

    public String execute(Player on) {
        return DataSource.sync(this, on);
    }
}
