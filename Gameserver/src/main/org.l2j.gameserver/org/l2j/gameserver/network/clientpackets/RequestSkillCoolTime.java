package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.SkillCoolTime;

public class RequestSkillCoolTime extends ClientPacket {

    @Override
    protected void runImpl() {

    }

    @Override
    protected void readImpl() throws Exception {
        client.sendPacket(new SkillCoolTime(client.getActiveChar()));
    }
}
