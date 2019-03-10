package org.l2j.gameserver.mobius.gameserver.network.serverpackets.ceremonyofchaos;

import org.l2j.gameserver.mobius.gameserver.enums.CeremonyOfChaosResult;
import org.l2j.gameserver.mobius.gameserver.instancemanager.CeremonyOfChaosManager;
import org.l2j.gameserver.mobius.gameserver.model.ceremonyofchaos.CeremonyOfChaosEvent;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExCuriousHouseResult extends IClientOutgoingPacket {
    private final CeremonyOfChaosResult _result;
    private final CeremonyOfChaosEvent _event;

    public ExCuriousHouseResult(CeremonyOfChaosResult result, CeremonyOfChaosEvent event) {
        _result = result;
        _event = event;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_CURIOUS_HOUSE_RESULT.writeId(packet);
        packet.putInt(_event.getId());
        packet.putShort((short) _result.ordinal());
        packet.putInt(CeremonyOfChaosManager.getInstance().getMaxPlayersInArena());
        packet.putInt(_event.getMembers().size());
        _event.getMembers().values().forEach(m ->
        {
            packet.putInt(m.getObjectId());
            packet.putInt(m.getPosition());
            packet.putInt(m.getClassId());
            packet.putInt(m.getLifeTime());
            packet.putInt(m.getScore());
        });
    }

}
