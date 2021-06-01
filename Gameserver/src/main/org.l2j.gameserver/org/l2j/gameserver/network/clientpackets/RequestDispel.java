/*
 * Copyright © 2019-2021 L2JOrg
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

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.settings.CharacterSettings;

/**
 * @author KenM
 */
public class RequestDispel extends ClientPacket {
    private int _objectId;
    private int _skillId;
    private int _skillLevel;

    @Override
    public void readImpl() {
        _objectId = readInt();
        _skillId = readInt();
        _skillLevel = readShort();
//        _skillSubLevel = readShort();
    }

    @Override
    public void runImpl() {
        if ((_skillId <= 0) || (_skillLevel <= 0)) {
            return;
        }
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }
        final Skill skill = SkillEngine.getInstance().getSkill(_skillId, _skillLevel);
        if (skill == null) {
            return;
        }
        if (!skill.canBeDispelled() || skill.isDebuff()) {
            return;
        }
        if (skill.getAbnormalType() == AbnormalType.TRANSFORM) {
            return;
        }
        if (skill.isDance() && !CharacterSettings.dispelDanceAllowed()) {
            return;
        }
        if (player.getObjectId() == _objectId) {
            player.stopSkillEffects(true, _skillId);
        } else {
            final Summon pet = player.getPet();
            if ((pet != null) && (pet.getObjectId() == _objectId)) {
                pet.stopSkillEffects(true, _skillId);
            }

            final Summon servitor = player.getServitor(_objectId);
            if (servitor != null) {
                servitor.stopSkillEffects(true, _skillId);
            }
        }
    }
}
