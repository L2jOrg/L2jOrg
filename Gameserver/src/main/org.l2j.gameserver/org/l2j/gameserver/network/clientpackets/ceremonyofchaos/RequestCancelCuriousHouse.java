package org.l2j.gameserver.network.clientpackets.ceremonyofchaos;

import org.l2j.gameserver.enums.CeremonyOfChaosState;
import org.l2j.gameserver.instancemanager.CeremonyOfChaosManager;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.ceremonyofchaos.ExCuriousHouseState;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestCancelCuriousHouse extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        if (CeremonyOfChaosManager.getInstance().unregisterPlayer(player)) {
            player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_TAKEN_OFF_THE_WAIT_LIST_YOU_MAY_ONLY_ENTER_THE_WAIT_LIST_ON_MON_THURS_EVERY_QUARTER_OF_AN_HOUR_FOR_5_MINUTES_BETWEEN_20_00_AND_23_40_IF_YOU_CANCEL_REGISTRATION_OR_CHOOSE_TO_FORFEIT_AFTER_ENTERING_A_MATCH_30_TIMES_OR_MORE_DURING_A_CYCLE_YOU_MUST_WAIT_UNTIL_THE_NEXT_CYCLE_TO_PARTICIPATE_IN_THE_CEREMONY_OF_CHAOS_UPON_ENTERING_THE_ARENA_ALL_BUFFS_EXCLUDING_VITALITY_BUFFS_ARE_REMOVED);
            player.sendPacket(ExCuriousHouseState.IDLE_PACKET);

            if (CeremonyOfChaosManager.getInstance().getState() == CeremonyOfChaosState.PREPARING_FOR_TELEPORT) {
                player.prohibiteCeremonyOfChaos();
            }
        }
    }
}
