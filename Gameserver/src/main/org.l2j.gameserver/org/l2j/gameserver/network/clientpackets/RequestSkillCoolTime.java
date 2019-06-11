package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.SkillCoolTime;

import java.nio.ByteBuffer;

public class RequestSkillCoolTime extends IClientIncomingPacket {

    @Override
    protected void runImpl() {

    }

    @Override
    protected void readImpl() throws Exception {
        client.sendPacket(new SkillCoolTime(client.getActiveChar()));
    }
}
