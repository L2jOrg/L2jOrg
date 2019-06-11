package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * @version 1.4
 */
public final class RequestSkillList extends IClientIncomingPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final L2PcInstance cha = client.getActiveChar();
        if (cha != null) {
            cha.sendSkillList();
        }
    }
}