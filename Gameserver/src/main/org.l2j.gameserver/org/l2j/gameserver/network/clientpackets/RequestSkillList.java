package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @version 1.4
 */
public final class RequestSkillList extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player cha = client.getActiveChar();
        if (cha != null) {
            cha.sendSkillList();
        }
    }
}