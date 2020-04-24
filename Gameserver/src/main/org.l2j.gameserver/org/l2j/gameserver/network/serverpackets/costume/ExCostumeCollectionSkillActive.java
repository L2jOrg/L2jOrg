package org.l2j.gameserver.network.serverpackets.costume;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author JoeAlisson
 */
public class ExCostumeCollectionSkillActive extends ServerPacket {

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerExPacketId.EX_COSTUME_COLLECTION_SKILL_ACTIVE);

        var collection = client.getPlayer().getActiveCostumeCollection();
        writeInt(collection.getId());
        writeInt(collection.getReuseTime());
    }
}
