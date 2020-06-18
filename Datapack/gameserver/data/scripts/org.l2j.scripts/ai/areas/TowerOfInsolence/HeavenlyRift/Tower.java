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
package ai.areas.TowerOfInsolence.HeavenlyRift;

import ai.AbstractNpcAI;
import ai.areas.TowerOfInsolence.TowerOfInsolence;
import org.l2j.gameserver.instancemanager.GlobalVariablesManager;
import org.l2j.gameserver.model.HeavenlyRift;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.util.GameUtils;


/**
 * @reworked by Thoss
 */
public class Tower extends AbstractNpcAI {
    public Tower() {
        addDeathId(18004);
    }

    @Override
    public void onCreatureKill(Creature creature, Creature killer) {
        HeavenlyRift.getZone().broadcastPacket(new ExShowScreenMessage(NpcStringId.YOU_HAVE_FAILED, 2, 5000));
        HeavenlyRift.getZone().forEachCreature(riftMonster -> riftMonster.decayMe(), riftMonster -> GameUtils.isMonster(riftMonster) && riftMonster.getId() == 20139 && !riftMonster.isDead());
        GlobalVariablesManager.getInstance().set("heavenly_rift_complete", GlobalVariablesManager.getInstance().getInt("heavenly_rift_level", 0));
        GlobalVariablesManager.getInstance().set("heavenly_rift_level", 0);
        GlobalVariablesManager.getInstance().set("heavenly_rift_reward", 0);
    }

    public static AbstractNpcAI provider() {
        return new Tower();
    }
}
