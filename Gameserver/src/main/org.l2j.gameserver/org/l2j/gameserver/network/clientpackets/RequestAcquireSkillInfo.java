/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.network.serverpackets.AcquireSkillInfo;
import org.l2j.gameserver.network.serverpackets.ExAcquireSkillInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.gameserver.util.GameUtils.isNpc;

/**
 * Request Acquire Skill Info client packet implementation.
 *
 * @author Zoey76
 */
public final class RequestAcquireSkillInfo extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAcquireSkillInfo.class);
    private int _id;
    private int _level;
    private AcquireSkillType _skillType;

    @Override
    public void readImpl() {
        _id = readInt();
        _level = readInt();
        _skillType = AcquireSkillType.getAcquireSkillType(readInt());
    }

    @Override
    public void runImpl() {
        if ((_id <= 0) || (_level <= 0)) {
            LOGGER.warn(RequestAcquireSkillInfo.class.getSimpleName() + ": Invalid Id: " + _id + " or level: " + _level + "!");
            return;
        }

        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final Npc trainer = activeChar.getLastFolkNPC();
        if ((_skillType != AcquireSkillType.CLASS) && (!isNpc(trainer) || (!trainer.canInteract(activeChar) && !activeChar.isGM()))) {
            return;
        }

        final Skill skill = SkillEngine.getInstance().getSkill(_id, _level);
        if (skill == null) {
            LOGGER.warn("Skill Id: " + _id + " level: " + _level + " is undefined. " + RequestAcquireSkillInfo.class.getName() + " failed.");
            return;
        }

        final SkillLearn s = SkillTreesData.getInstance().getSkillLearn(_skillType, _id, _level, activeChar);
        if (s == null) {
            return;
        }

        switch (_skillType) {
            case TRANSFORM, FISHING -> client.sendPacket(new AcquireSkillInfo(_skillType, s));
            case CLASS -> client.sendPacket(new ExAcquireSkillInfo(activeChar, s));
            case PLEDGE -> {
                if (!activeChar.isClanLeader()) {
                    return;
                }
                client.sendPacket(new AcquireSkillInfo(_skillType, s));
            }
            case SUBPLEDGE -> {
                if (!activeChar.isClanLeader() || !activeChar.hasClanPrivilege(ClanPrivilege.CL_TROOPS_FAME)) {
                    return;
                }
                client.sendPacket(new AcquireSkillInfo(_skillType, s));
            }
        }
    }
}
