package org.l2j.gameserver.model;

/**
 * * @author Gnacik
 */
public class PremiumItem {
    private final int _itemId;
    private final String _sender;
    private long _count;

    public PremiumItem(int itemid, long count, String sender) {
        _itemId = itemid;
        _count = count;
        _sender = sender;
    }

    public void updateCount(long newcount) {
        _count = newcount;
    }

    public int getItemId() {
        return _itemId;
    }

    public long getCount() {
        return _count;
    }

    public String getSender() {
        return _sender;
    }
}
