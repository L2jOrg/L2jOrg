package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.model.TimeStamp;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Skill Cool Time server packet implementation.
 *
 * @author KenM, Zoey76
 */
public class SkillCoolTime extends ServerPacket {
    private final List<TimeStamp> _skillReuseTimeStamps = new ArrayList<>();

    public SkillCoolTime(Player player) {
        final Map<Long, TimeStamp> skillReuseTimeStamps = player.getSkillReuseTimeStamps();
        if (skillReuseTimeStamps != null) {
            for (TimeStamp ts : skillReuseTimeStamps.values()) {
                final Skill skill = SkillData.getInstance().getSkill(ts.getSkillId(), ts.getSkillLvl(), ts.getSkillSubLvl());
                if (ts.hasNotPassed() && !skill.isNotBroadcastable()) {
                    _skillReuseTimeStamps.add(ts);
                }
            }
        }
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.SKILL_COOL_TIME);

        writeInt(_skillReuseTimeStamps.size());
        for (TimeStamp ts : _skillReuseTimeStamps) {
            writeInt(ts.getSkillId());
            writeInt(0x00); // ?
            writeInt((int) ts.getReuse() / 1000);
            writeInt((int) ts.getRemaining() / 1000);
        }
    }

}
