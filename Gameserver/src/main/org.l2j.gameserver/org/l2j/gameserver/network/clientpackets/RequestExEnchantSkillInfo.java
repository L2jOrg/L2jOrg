package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExEnchantSkillInfo;

import java.util.Collections;
import java.util.Set;

/**
 * Format (ch) dd c: (id) 0xD0 h: (subid) 0x06 d: skill id d: skill lvl
 *
 * @author -Wooden-
 */
public final class RequestExEnchantSkillInfo extends ClientPacket {
    private int _skillId;
    private int _skillLvl;
    private int _skillSubLvl;

    @Override
    public void readImpl() {
        _skillId = readInt();
        _skillLvl = readShort();
        _skillSubLvl = readShort();
    }

    @Override
    public void runImpl() {
        if ((_skillId <= 0) || (_skillLvl <= 0) || (_skillSubLvl < 0)) {
            return;
        }

        final Player activeChar = client.getPlayer();

        if (activeChar == null) {
            return;
        }

        final Skill skill = SkillEngine.getInstance().getSkill(_skillId, _skillLvl);
        if ((skill == null) || (skill.getId() != _skillId)) {
            return;
        }
        final Set<Integer> route = Collections.emptySet();
        if (route.isEmpty()) {
            return;
        }

        final Skill playerSkill = activeChar.getKnownSkill(_skillId);
        if ((playerSkill.getLevel() != _skillLvl) || (playerSkill.getSubLevel() != _skillSubLvl)) {
            return;
        }

        client.sendPacket(new ExEnchantSkillInfo(_skillId, _skillLvl, _skillSubLvl, playerSkill.getSubLevel()));
    }
}