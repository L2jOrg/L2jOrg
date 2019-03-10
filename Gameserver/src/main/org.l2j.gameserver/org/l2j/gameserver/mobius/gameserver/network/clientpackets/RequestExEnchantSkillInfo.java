package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.EnchantSkillGroupsData;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.mobius.gameserver.enums.CategoryType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExEnchantSkillInfo;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * Format (ch) dd c: (id) 0xD0 h: (subid) 0x06 d: skill id d: skill lvl
 *
 * @author -Wooden-
 */
public final class RequestExEnchantSkillInfo extends IClientIncomingPacket {
    private int _skillId;
    private int _skillLvl;
    private int _skillSubLvl;

    @Override
    public void readImpl(ByteBuffer packet) {
        _skillId = packet.getInt();
        _skillLvl = packet.getShort();
        _skillSubLvl = packet.getShort();
    }

    @Override
    public void runImpl() {
        if ((_skillId <= 0) || (_skillLvl <= 0) || (_skillSubLvl < 0)) {
            return;
        }

        final L2PcInstance activeChar = client.getActiveChar();

        if (activeChar == null) {
            return;
        }

        if (!activeChar.isInCategory(CategoryType.SIXTH_CLASS_GROUP)) {
            return;
        }

        final Skill skill = SkillData.getInstance().getSkill(_skillId, _skillLvl, _skillSubLvl);
        if ((skill == null) || (skill.getId() != _skillId)) {
            return;
        }
        final Set<Integer> route = EnchantSkillGroupsData.getInstance().getRouteForSkill(_skillId, _skillLvl);
        if (route.isEmpty()) {
            return;
        }

        final Skill playerSkill = activeChar.getKnownSkill(_skillId);
        if ((playerSkill.getLevel() != _skillLvl) || (playerSkill.getSubLevel() != _skillSubLvl)) {
            return;
        }

        client.sendPacket(new ExEnchantSkillInfo(_skillId, _skillLvl, _skillSubLvl, playerSkill.getSubLevel()));
        // ExEnchantSkillInfoDetail - not really necessary I think
        // client.sendPacket(new ExEnchantSkillInfoDetail(SkillEnchantType.NORMAL, _skillId, _skillLvl, _skillSubLvl, activeChar));
    }
}