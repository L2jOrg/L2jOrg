package org.l2j.gameserver.network.l2.s2c;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;

@StaticPacket
public class ExSendManorListPacket  extends L2GameServerPacket{
    
    public static final ExSendManorListPacket STATIC_PACKET = new ExSendManorListPacket();
    
    private ExSendManorListPacket(){ 
        // only Static access
    }
    
    @Override
    protected void writeImpl(GameClient client, ByteBuffer buffer) {
        var castles = ResidenceHolder.getInstance().getResidenceList(Castle.class);

        buffer.putInt(castles.size());

        for (var castle : castles) {
            buffer.putInt(castle.getId());
        }

    }
}
