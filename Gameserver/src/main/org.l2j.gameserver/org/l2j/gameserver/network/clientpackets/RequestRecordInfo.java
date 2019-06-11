package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.UserInfo;

import java.nio.ByteBuffer;

public class RequestRecordInfo extends IClientIncomingPacket {
    @Override
    public void readImpl() {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
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
                    // the L2PcInstance
                    final L2Character obj = (L2Character) object;
                    if (obj.getAI() != null) {
                        obj.getAI().describeStateToPlayer(activeChar);
                    }
                }
            }
        });
    }
}
