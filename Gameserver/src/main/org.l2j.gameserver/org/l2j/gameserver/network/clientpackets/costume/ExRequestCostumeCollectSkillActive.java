package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.engine.costume.CostumeEngine;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.costume.ExCostumeCollectionSkillActive;

/**
 * @author JoeAlisson
 */
public class ExRequestCostumeCollectSkillActive extends ClientPacket {

    private int collectionId;

    @Override
    protected void readImpl() throws Exception {
        collectionId = readInt();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        if(CostumeEngine.getInstance().activeCollection(player, collectionId)) {
            client.sendPacket(new ExCostumeCollectionSkillActive());
        }
    }
}
