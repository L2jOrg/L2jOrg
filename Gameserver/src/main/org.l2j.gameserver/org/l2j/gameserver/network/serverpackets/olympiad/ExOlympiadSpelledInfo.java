package org.l2j.gameserver.network.serverpackets.olympiad;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.ArrayList;
import java.util.List;

/**
 * @author godson
 */
public class ExOlympiadSpelledInfo extends ServerPacket {
    private final int _playerId;
    private final List<BuffInfo> _effects = new ArrayList<>();
    private final List<Skill> _effects2 = new ArrayList<>();

    public ExOlympiadSpelledInfo(Player player) {
        _playerId = player.getObjectId();
    }

    public void addSkill(BuffInfo info) {
        _effects.add(info);
    }

    public void addSkill(Skill skill) {
        _effects2.add(skill);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_OLYMPIAD_SPELLED_INFO);

        writeInt(_playerId);
        writeInt(_effects.size() + _effects2.size());
        for (BuffInfo info : _effects) {
            if ((info != null) && info.isInUse()) {
                writeInt(info.getSkill().getDisplayId());
                writeShort((short) info.getSkill().getDisplayLevel());
                writeShort((short) 0x00); // Sub level
                writeInt(info.getSkill().getAbnormalType().getClientId());
                writeOptionalD(info.getSkill().isAura() ? -1 : info.getTime());
            }
        }
        for (Skill skill : _effects2) {
            if (skill != null) {
                writeInt(skill.getDisplayId());
                writeShort((short) skill.getDisplayLevel());
                writeShort((short) 0x00); // Sub level
                writeInt(skill.getAbnormalType().getClientId());
                writeShort((short) -1);
            }
        }
    }

}
