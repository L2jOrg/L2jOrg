package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.UserInfo;

import static org.l2j.gameserver.util.GameUtils.isCreature;

public class RequestRecordInfo extends ClientPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        client.sendPacket(new UserInfo(activeChar));

        World.getInstance().forEachVisibleObject(activeChar, WorldObject.class, object ->
        {
            if (object.isVisibleFor(activeChar)) {
                object.sendInfo(activeChar);

                if (isCreature(object)) {
                    // Update the state of the Creature object client
                    // side by sending Server->Client packet
                    // MoveToPawn/CharMoveToLocation and AutoAttackStart to
                    // the Player
                    final Creature obj = (Creature) object;
                    if (obj.getAI() != null) {
                        obj.getAI().describeStateToPlayer(activeChar);
                    }
                }
            }
        });
    }
}
