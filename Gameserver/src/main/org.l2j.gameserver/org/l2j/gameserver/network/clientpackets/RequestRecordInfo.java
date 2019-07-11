package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.UserInfo;

public class RequestRecordInfo extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new UserInfo(activeChar));

        L2World.getInstance().forEachVisibleObject(activeChar, L2Object.class, object ->
        {
            if (object.isVisibleFor(activeChar)) {
                object.sendInfo(activeChar);

                if (object.isCharacter()) {
                    // Update the state of the L2Character object client
                    // side by sending Server->Client packet
                    // MoveToPawn/CharMoveToLocation and AutoAttackStart to
                    // the Player
                    final L2Character obj = (L2Character) object;
                    if (obj.getAI() != null) {
                        obj.getAI().describeStateToPlayer(activeChar);
                    }
                }
            }
        });
    }
}
