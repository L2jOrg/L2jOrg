package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.database.data.CrestData;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.network.serverpackets.ExPledgeEmblem;

/**
 * @author -Wooden-, Sdw
 */
public final class RequestExPledgeCrestLarge extends ClientPacket {
    private int _crestId;
    private int _clanId;

    @Override
    public void readImpl() {
        _crestId = readInt();
        _clanId = readInt();
    }

    @Override
    public void runImpl() {
        final CrestData crest = CrestTable.getInstance().getCrest(_crestId);
        final byte[] data = crest != null ? crest.getData() : null;
        if (data != null) {
            for (int i = 0; i <= 4; i++) {
                if (i < 4) {
                    final byte[] fullChunk = new byte[14336];
                    System.arraycopy(data, (14336 * i), fullChunk, 0, 14336);
                    client.sendPacket(new ExPledgeEmblem(_crestId, fullChunk, _clanId, i));
                } else {
                    final byte[] lastChunk = new byte[8320];
                    System.arraycopy(data, (14336 * i), lastChunk, 0, 8320);
                    client.sendPacket(new ExPledgeEmblem(_crestId, lastChunk, _clanId, i));
                }
            }
        }
    }
}
