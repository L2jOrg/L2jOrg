package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchList;

/**
 * Format: (ch)d d: unknown (always 0?)
 *
 * @author mrTJO
 */
public class RequestExOlympiadMatchListRefresh extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        activeChar.sendPacket(new ExOlympiadMatchList());
    }
}