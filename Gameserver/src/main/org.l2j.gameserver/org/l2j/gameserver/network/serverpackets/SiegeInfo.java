package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Calendar;

import static java.util.Objects.nonNull;

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
public class SiegeInfo extends IClientOutgoingPacket {
    private final Castle _castle;
    private final L2PcInstance _player;

    public SiegeInfo(Castle castle, L2PcInstance player) {
        _castle = castle;
        _player = player;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.CASTLE_SIEGE_INFO.writeId(packet);

        if (_castle != null) {
            packet.putInt(_castle.getResidenceId());

            final int ownerId = _castle.getOwnerId();

            packet.putInt(((ownerId == _player.getClanId()) && (_player.isClanLeader())) ? 0x01 : 0x00);
            packet.putInt(ownerId);
            if (ownerId > 0) {
                final L2Clan owner = ClanTable.getInstance().getClan(ownerId);
                if (owner != null) {
                    writeString(owner.getName(), packet); // Clan Name
                    writeString(owner.getLeaderName(), packet); // Clan Leader Name
                    packet.putInt(owner.getAllyId()); // Ally ID
                    writeString(owner.getAllyName(), packet); // Ally Name
                } else {
                    LOGGER.warn("Null owner for castle: " + _castle.getName());
                }
            } else {
                writeString("", packet); // Clan Name
                writeString("", packet); // Clan Leader Name
                packet.putInt(0); // Ally ID
                writeString("", packet); // Ally Name
            }

            packet.putInt((int) (System.currentTimeMillis() / 1000));
            if (!_castle.getIsTimeRegistrationOver() && _player.isClanLeader() && (_player.getClanId() == _castle.getOwnerId())) {
                final Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(_castle.getSiegeDate().getTimeInMillis());
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);

                packet.putInt(0x00);
                packet.putInt(Config.SIEGE_HOUR_LIST.size());
                for (int hour : Config.SIEGE_HOUR_LIST) {
                    cal.set(Calendar.HOUR_OF_DAY, hour);
                    packet.putInt((int) (cal.getTimeInMillis() / 1000));
                }
            } else {
                packet.putInt((int) (_castle.getSiegeDate().getTimeInMillis() / 1000));
                packet.putInt(0x00);
            }
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 140 + ( nonNull(_castle) ? 34 + Config.SIEGE_HOUR_LIST.size() * 4 : 0);
    }
}
