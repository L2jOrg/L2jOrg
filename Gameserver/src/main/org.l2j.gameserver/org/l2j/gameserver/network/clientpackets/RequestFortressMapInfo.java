package org.l2j.gameserver.network.clientpackets;

/**
 * @author KenM
 */
public class RequestFortressMapInfo extends ClientPacket {
    private int _fortressId;

    @Override
    public void readImpl() {
        _fortressId = readInt();
    }

    @Override
    public void runImpl() {
    }
}
