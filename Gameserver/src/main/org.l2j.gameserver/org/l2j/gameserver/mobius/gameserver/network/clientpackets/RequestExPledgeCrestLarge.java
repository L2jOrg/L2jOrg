package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.mobius.gameserver.model.L2Crest;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExPledgeEmblem;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-, Sdw
 */
public final class RequestExPledgeCrestLarge extends IClientIncomingPacket
{
    private int _crestId;
    private int _clanId;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _crestId = packet.getInt();
        _clanId = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2Crest crest = CrestTable.getInstance().getCrest(_crestId);
        final byte[] data = crest != null ? crest.getData() : null;
        if (data != null)
        {
            for (int i = 0; i <= 4; i++)
            {
                if (i < 4)
                {
                    final byte[] fullChunk = new byte[14336];
                    System.arraycopy(data, (14336 * i), fullChunk, 0, 14336);
                    client.sendPacket(new ExPledgeEmblem(_crestId, fullChunk, _clanId, i));
                }
                else
                {
                    final byte[] lastChunk = new byte[8320];
                    System.arraycopy(data, (14336 * i), lastChunk, 0, 8320);
                    client.sendPacket(new ExPledgeEmblem(_crestId, lastChunk, _clanId, i));
                }
            }
        }
    }
}
