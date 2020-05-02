package org.l2j.gameserver.network.serverpackets.item;

/**
 * @author JoeAlisson
 */
public enum ItemPacketType {
    HEADER,
    LIST;

    public int clientId() {
        return 1 + ordinal();
    }
}
