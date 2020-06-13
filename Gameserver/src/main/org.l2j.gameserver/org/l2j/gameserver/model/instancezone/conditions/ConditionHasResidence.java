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
package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.enums.ResidenceType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

/**
 * Instance residence condition
 *
 * @author malyelfik
 */
public final class ConditionHasResidence extends Condition {
    public ConditionHasResidence(InstanceTemplate template, StatsSet parameters, boolean onlyLeader, boolean showMessageAndHtml) {
        super(template, parameters, onlyLeader, showMessageAndHtml);
    }

    @Override
    protected boolean test(Player player, Npc npc) {
        final Clan clan = player.getClan();
        if (clan == null) {
            return false;
        }

        final StatsSet params = getParameters();
        final int id = params.getInt("id");
        boolean test = false;
        switch (params.getEnum("type", ResidenceType.class)) {
            case CASTLE: {
                test = clan.getCastleId() == id;
                break;
            }
            case CLANHALL: {
                test = clan.getHideoutId() == id;
                break;
            }
        }
        return test;
    }
}