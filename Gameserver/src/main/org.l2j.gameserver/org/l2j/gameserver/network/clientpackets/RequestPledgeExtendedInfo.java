package org.l2j.gameserver.network.clientpackets;

/**
 * Format: (c) S S: pledge name?
 *
 * @author -Wooden-
 */
public class RequestPledgeExtendedInfo extends ClientPacket {
    @SuppressWarnings("unused")
    private String _name;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        // TODO: Implement
    }
}
