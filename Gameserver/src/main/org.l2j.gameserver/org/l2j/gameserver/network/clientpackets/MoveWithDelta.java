package org.l2j.gameserver.network.clientpackets;

/**
 * Format: (c) ddd d: dx d: dy d: dz
 *
 * @author -Wooden-
 */
public class MoveWithDelta extends ClientPacket {
    @SuppressWarnings("unused")
    private int _dx;
    @SuppressWarnings("unused")
    private int _dy;
    @SuppressWarnings("unused")
    private int _dz;

    @Override
    public void readImpl() {
        _dx = readInt();
        _dy = readInt();
        _dz = readInt();
    }

    @Override
    public void runImpl() {
        // TODO this
    }
}
