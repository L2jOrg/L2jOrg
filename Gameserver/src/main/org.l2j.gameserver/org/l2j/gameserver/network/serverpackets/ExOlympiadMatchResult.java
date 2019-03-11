package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.olympiad.OlympiadInfo;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * @author JIV
 */
public class ExOlympiadMatchResult extends IClientOutgoingPacket {
    private final boolean _tie;
    private final List<OlympiadInfo> _winnerList;
    private final List<OlympiadInfo> _loserList;
    private int _winTeam; // 1,2
    private int _loseTeam = 2;

    public ExOlympiadMatchResult(boolean tie, int winTeam, List<OlympiadInfo> winnerList, List<OlympiadInfo> loserList) {
        _tie = tie;
        _winTeam = winTeam;
        _winnerList = winnerList;
        _loserList = loserList;

        if (_winTeam == 2) {
            _loseTeam = 1;
        } else if (_winTeam == 0) {
            _winTeam = 1;
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_RECEIVE_OLYMPIAD.writeId(packet);

        packet.putInt(0x01); // Type 0 = Match List, 1 = Match Result

        packet.putInt(_tie ? 1 : 0); // 0 - win, 1 - tie
        writeString(_winnerList.get(0).getName(), packet);
        packet.putInt(_winTeam);
        packet.putInt(_winnerList.size());
        for (OlympiadInfo info : _winnerList) {
            writeString(info.getName(), packet);
            writeString(info.getClanName(), packet);
            packet.putInt(info.getClanId());
            packet.putInt(info.getClassId());
            packet.putInt(info.getDamage());
            packet.putInt(info.getCurrentPoints());
            packet.putInt(info.getDiffPoints());
            packet.putInt(0x00); // Helios
        }

        packet.putInt(_loseTeam);
        packet.putInt(_loserList.size());
        for (OlympiadInfo info : _loserList) {
            writeString(info.getName(), packet);
            writeString(info.getClanName(), packet);
            packet.putInt(info.getClanId());
            packet.putInt(info.getClassId());
            packet.putInt(info.getDamage());
            packet.putInt(info.getCurrentPoints());
            packet.putInt(info.getDiffPoints());
            packet.putInt(0x00); // Helios
        }
    }
}
