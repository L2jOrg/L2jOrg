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
                final int size = Math.max(Math.min(14336, data.length - (14336 * i)), 0);
                if (size == 0)
                {
                    continue;
                }
                final byte[] chunk = new byte[size];
                System.arraycopy(data, (14336 * i), chunk, 0, size);
                client.sendPacket(new ExPledgeEmblem(_crestId, chunk, _clanId, i));
            }
        }
    }
}
