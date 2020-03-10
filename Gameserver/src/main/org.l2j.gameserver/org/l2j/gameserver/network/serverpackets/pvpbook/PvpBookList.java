package org.l2j.gameserver.network.serverpackets.pvpbook;

import org.l2j.gameserver.data.database.data.KillerData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.world.World;

import java.util.List;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class PvpBookList extends ServerPacket {

    private final List<KillerData> killers;

    public PvpBookList(List<KillerData> killers) {
        this.killers = killers;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_PVPBOOK_LIST);
        var player = client.getPlayer();
        player.getVariables();

        writeInt(4); // show killers location count
        writeInt(5); // teleport count

        writeInt(killers.size());
        for (KillerData killer : killers) {
            writeSizedString(killer.getName());
            writeSizedString(killer.getClan());
            writeInt(killer.getLevel());
            writeInt(killer.getRace());
            writeInt(killer.getActiveClass());
            writeInt(killer.getKillTime());
            writeByte(nonNull(World.getInstance().findObject(killer.getKilleId())));
        }
    }
}
