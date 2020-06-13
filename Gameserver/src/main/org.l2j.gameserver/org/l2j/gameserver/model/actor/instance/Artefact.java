/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

/**
 * This class manages all Castle Siege Artefacts.<BR>
 * <BR>
 *
 * @version $Revision: 1.11.2.1.2.7 $ $Date: 2005/04/06 16:13:40 $
 */
public final class Artefact extends Npc {
    /**
     * Constructor of Artefact (use Creature and Folk constructor).<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>Call the Creature constructor to set the _template of the Artefact (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li>
     * <li>Set the name of the Artefact</li>
     * <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server allow it</li><BR>
     * <BR>
     *
     * @param template to apply to the NPC
     */
    public Artefact(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2ArtefactInstance);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        getCastle().registerArtefact(this);
    }

    /**
     * Return False.
     */
    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    public boolean canBeAttacked() {
        return false;
    }

    @Override
    public void onForcedAttack(Player player) {
        // Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, Skill skill, DamageInfo.DamageType zone) {
    }

    @Override
    public void reduceCurrentHp(double value, Creature attacker, Skill skill, boolean isDOT, boolean directlyToHp, boolean critical, boolean reflect, DamageInfo.DamageType drown) {
    }
}
