package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SkillList extends IClientOutgoingPacket {
    private final List<Skill> _skills = new ArrayList<>();
    private int _lastLearnedSkillId = 0;

    public void addSkill(int id, int reuseDelayGroup, int level, int subLevel, boolean passive, boolean disabled, boolean enchanted) {
        _skills.add(new Skill(id, reuseDelayGroup, level, subLevel, passive, disabled, enchanted));
    }

    public void setLastLearnedSkillId(int lastLearnedSkillId) {
        _lastLearnedSkillId = lastLearnedSkillId;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SKILL_LIST.writeId(packet);
        _skills.sort(Comparator.comparing(s -> SkillData.getInstance().getSkill(s.id, s.level, s.subLevel).isToggle() ? 1 : 0));
        packet.putInt(_skills.size());
        for (Skill temp : _skills) {
            packet.putInt(temp.passive ? 1 : 0);
            packet.putShort((short) temp.level);
            packet.putShort((short) temp.subLevel);
            packet.putInt(temp.id);
            packet.putInt(temp.reuseDelayGroup); // GOD ReuseDelayShareGroupID
            packet.put((byte) (temp.disabled ? 1 : 0)); // iSkillDisabled
            packet.put((byte) (temp.enchanted ? 1 : 0)); // CanEnchant
        }
        packet.putInt(_lastLearnedSkillId);
    }

    static class Skill {
        public int id;
        public int reuseDelayGroup;
        public int level;
        public int subLevel;
        public boolean passive;
        public boolean disabled;
        public boolean enchanted;

        Skill(int pId, int pReuseDelayGroup, int pLevel, int pSubLevel, boolean pPassive, boolean pDisabled, boolean pEnchanted) {
            id = pId;
            reuseDelayGroup = pReuseDelayGroup;
            level = pLevel;
            subLevel = pSubLevel;
            passive = pPassive;
            disabled = pDisabled;
            enchanted = pEnchanted;
        }
    }
}
