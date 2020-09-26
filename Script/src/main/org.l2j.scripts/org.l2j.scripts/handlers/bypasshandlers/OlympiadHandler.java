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
package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.StringTokenizer;

/**
 * @author JoeAlisson
 */
public class OlympiadHandler implements IBypassHandler {

    @Override
    public boolean useBypass(String bypass, Player player, Creature bypassOrigin) {
        var tokens = new StringTokenizer(bypass);
        tokens.nextToken();
        if(tokens.hasMoreTokens()) {
            String command = tokens.nextToken();
            if(command.equalsIgnoreCase("start_match_making")) {
                Olympiad.getInstance().startMatchMaking(player);
            }
        }


        return false;
    }

    @Override
    public String[] getBypassList() {
        return new String[] {"Olympiad"};
    }
}
