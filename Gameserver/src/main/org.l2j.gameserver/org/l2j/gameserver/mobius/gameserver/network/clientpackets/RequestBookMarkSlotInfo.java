package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExGetBookMarkInfoPacket;

import java.nio.ByteBuffer;

/**
 * @author ShanSoft Packets Structure: chddd
 */
public final class RequestBookMarkSlotInfo extends IClientIncomingPacket {
    public RequestBookMarkSlotInfo(L2GameClient client) {
        this.client = client;
    }

    @Override
    public void readImpl(ByteBuffer packet) {
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player != null) {
            player.sendPacket(new ExGetBookMarkInfoPacket(player));
        }
    }
}
