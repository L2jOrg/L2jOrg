package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.TimeStamp;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

/**
 * Skill Cool Time server packet implementation.
 *
 * @author KenM, Zoey76, Mobius
 */
public class SkillCoolTime extends ServerPacket {
    private final long _currentTime;
    private final List<TimeStamp> _skillReuseTimeStamps = new ArrayList<>();

    public SkillCoolTime(Player player) {
        _currentTime = System.currentTimeMillis();
        for (TimeStamp ts : player.getSkillReuseTimeStamps().values())
        {
            if ((_currentTime < ts.getStamp()) && !SkillEngine.getInstance().getSkill(ts.getSkillId(), ts.getSkillLvl()).isNotBroadcastable())
            {
                _skillReuseTimeStamps.add(ts);
            }
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SKILL_COOL_TIME);

        writeInt(_skillReuseTimeStamps.size());
        for (TimeStamp ts : _skillReuseTimeStamps) {
            writeInt(ts.getSkillId());
            writeInt(0x00); // ?
            writeInt((int) ts.getReuse() / 1000);
            writeInt((int) Math.max(ts.getStamp() - _currentTime, 0) / 1000);
        }
    }

}
