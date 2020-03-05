package org.l2j.gameserver.enums;

/**
 * @author St3eT
 */
public enum ClanHallType {
    AUCTIONABLE(0),
    SIEGEABLE(1),
    OTHER(2);

    private final int _clientVal;

    ClanHallType(int clientVal) {
        _clientVal = clientVal;
    }

    public int getClientVal() {
        return _clientVal;
    }
}