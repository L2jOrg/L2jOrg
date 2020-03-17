package org.l2j.gameserver.network.clientpackets.pvpbook;

import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.pvpbook.PvpBookList;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 */
public class ExRequestPvpBookList extends ClientPacket {

    @Override
    protected void readImpl() {
        // dummy byte
    }

    @Override
    protected void runImpl()  {
        var since = Instant.now().minus(1, ChronoUnit.DAYS).getEpochSecond();
        var killers = getDAO(PlayerDAO.class).findKillersByPlayer(client.getPlayer().getObjectId(), since);
        client.sendPacket(new PvpBookList(killers));
    }
}
