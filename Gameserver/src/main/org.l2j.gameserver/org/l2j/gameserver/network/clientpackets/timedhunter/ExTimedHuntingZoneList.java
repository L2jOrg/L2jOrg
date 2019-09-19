package org.l2j.gameserver.network.clientpackets.timedhunter;

import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.timedhunter.TimedHuntingZoneList;

public class ExTimedHuntingZoneList extends ClientPacket {

    @Override
    protected void readImpl() throws Exception {
        // trigger packet
    }

    @Override
    protected void runImpl() {
        client.sendPacket(new TimedHuntingZoneList());
    }
}
