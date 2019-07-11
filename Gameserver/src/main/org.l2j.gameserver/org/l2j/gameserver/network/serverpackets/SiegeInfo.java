package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * Shows the Siege Info<BR>
 * <BR>
 * c = c9<BR>
 * d = CastleID<BR>
 * d = Show Owner Controls (0x00 default || >=0x02(mask?) owner)<BR>
 * d = Owner ClanID<BR>
 * S = Owner ClanName<BR>
 * S = Owner Clan LeaderName<BR>
 * d = Owner AllyID<BR>
 * S = Owner AllyName<BR>
 * d = current time (seconds)<BR>
 * d = Siege time (seconds) (0 for selectable)<BR>
 * d = (UNKNOW) Siege Time Select Related?
 *
 * @author KenM
 */
public class SiegeInfo extends ServerPacket {

    private static final Logger LOGGER = LoggerFactory.getLogger(SiegeInfo.class);

    private final Castle _castle;
    private final Player _player;

    public SiegeInfo(Castle castle, Player player) {
        _castle = castle;
        _player = player;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.CASTLE_SIEGE_INFO);

        if (_castle != null) {
            writeInt(_castle.getResidenceId());

            final int ownerId = _castle.getOwnerId();

            writeInt(((ownerId == _player.getClanId()) && (_player.isClanLeader())) ? 0x01 : 0x00);
            writeInt(ownerId);
            if (ownerId > 0) {
                final L2Clan owner = ClanTable.getInstance().getClan(ownerId);
                if (owner != null) {
                    writeString(owner.getName()); // Clan Name
                    writeString(owner.getLeaderName()); // Clan Leader Name
                    writeInt(owner.getAllyId()); // Ally ID
                    writeString(owner.getAllyName()); // Ally Name
                } else {
                    LOGGER.warn("Null owner for castle: " + _castle.getName());
                }
            } else {
                writeString(""); // Clan Name
                writeString(""); // Clan Leader Name
                writeInt(0); // Ally ID
                writeString(""); // Ally Name
            }

            writeInt((int) (System.currentTimeMillis() / 1000));
            if (!_castle.getIsTimeRegistrationOver() && _player.isClanLeader() && (_player.getClanId() == _castle.getOwnerId())) {
                final Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(_castle.getSiegeDate().getTimeInMillis());
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);

                writeInt(0x00);
                writeInt(Config.SIEGE_HOUR_LIST.size());
                for (int hour : Config.SIEGE_HOUR_LIST) {
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    writeInt((int) (cal.getTimeInMillis() / 1000));
                }
            } else {
                writeInt((int) (_castle.getSiegeDate().getTimeInMillis() / 1000));
                writeInt(0x00);
            }
        }
    }

}
