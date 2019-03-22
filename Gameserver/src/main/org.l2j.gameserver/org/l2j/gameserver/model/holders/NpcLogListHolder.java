package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.network.NpcStringId;

/**
 * @author Sdw
 */
public class NpcLogListHolder {
    private final int _id;
    private final boolean _isNpcString;
    private final int _count;

    public NpcLogListHolder(NpcStringId npcStringId, int count) {
        _id = npcStringId.getId();
        _isNpcString = true;
        _count = count;
    }

    public NpcLogListHolder(int id, boolean isNpcString, int count) {
        _id = id;
        _isNpcString = isNpcString;
        _count = count;
    }

    public int getId() {
        return _id;
    }

    public boolean isNpcString() {
        return _isNpcString;
    }

    public int getCount() {
        return _count;
    }
}
