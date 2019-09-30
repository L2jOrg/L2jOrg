package org.l2j.gameserver.network.clientpackets;

/**
 * @author JoeAlisson
 */
public class ExAutoPlaySetting extends ClientPacket {

    @Override
    protected void readImpl() throws Exception {
        var options = readShort();
        var active = readByteAsBoolean();
        var pickUp = readByteAsBoolean();
        var nextTargetMode = readShort();
        var isNearTarget = readByteAsBoolean();
        var usableHpPotionPercent = readInt();
        var mannerMode = readByteAsBoolean();
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new org.l2j.gameserver.network.serverpackets.ExAutoPlaySetting());
    }
}
