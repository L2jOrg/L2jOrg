package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.ClanInfo;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.AllianceInfo;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1479 $ $Date: 2005-11-09 00:47:42 +0100 (mer., 09 nov. 2005) $
 */
public final class RequestAllyInfo extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        SystemMessage sm;
        final int allianceId = activeChar.getAllyId();
        if (allianceId > 0) {
            final AllianceInfo ai = new AllianceInfo(allianceId);
            client.sendPacket(ai);

            // send for player
            sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_INFORMATION);
            client.sendPacket(sm);

            sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_NAME_S1);
            sm.addString(ai.getName());
            client.sendPacket(sm);

            sm = SystemMessage.getSystemMessage(SystemMessageId.ALLIANCE_LEADER_S2_OF_S1);
            sm.addString(ai.getLeaderC());
            sm.addString(ai.getLeaderP());
            client.sendPacket(sm);

            sm = SystemMessage.getSystemMessage(SystemMessageId.CONNECTION_S1_TOTAL_S2);
            sm.addInt(ai.getOnline());
            sm.addInt(ai.getTotal());
            client.sendPacket(sm);

            sm = SystemMessage.getSystemMessage(SystemMessageId.AFFILIATED_CLANS_TOTAL_S1_CLAN_S);
            sm.addInt(ai.getAllies().length);
            client.sendPacket(sm);

            sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_INFORMATION);
            for (ClanInfo aci : ai.getAllies()) {
                client.sendPacket(sm);

                sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_NAME_S1);
                sm.addString(aci.getClan().getName());
                client.sendPacket(sm);

                sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEADER_S1);
                sm.addString(aci.getClan().getLeaderName());
                client.sendPacket(sm);

                sm = SystemMessage.getSystemMessage(SystemMessageId.CLAN_LEVEL_S1);
                sm.addInt(aci.getClan().getLevel());
                client.sendPacket(sm);

                sm = SystemMessage.getSystemMessage(SystemMessageId.CONNECTION_S1_TOTAL_S2);
                sm.addInt(aci.getOnline());
                sm.addInt(aci.getTotal());
                client.sendPacket(sm);

                sm = SystemMessage.getSystemMessage(SystemMessageId.EMPTY_4);
            }

            sm = SystemMessage.getSystemMessage(SystemMessageId.EMPTY_5);
            client.sendPacket(sm);
        } else {
            client.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
        }
    }
}
