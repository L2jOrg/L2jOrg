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

import static org.l2j.commons.util.Util.parseNextInt;

/**
 * @author JoeAlisson
 */
public class OlympiadHandler implements IBypassHandler {

    @Override
    public boolean useBypass(String bypass, Player player, Creature bypassOrigin) {
        bypass = bypass.substring(10).replaceAll("[\\w_]+=", "").trim();

        var tokens = new StringTokenizer(bypass, "&");
        if(tokens.hasMoreTokens()) {

            switch (tokens.nextToken()) {
                case "start_match_making" -> Olympiad.getInstance().startMatchMaking(player);
                case "op_field_list" -> Olympiad.getInstance().showMatchList(player);
                case "move_op_field" -> Olympiad.getInstance().addSpectator(player, parseNextInt(tokens, 0));
                case "hero_list" -> Olympiad.getInstance().showHeroList(player);
                case "claim_hero" -> Olympiad.getInstance().claimHero(player);
                case "change_points" -> Olympiad.getInstance().changePoints(player);
                default -> {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public String[] getBypassList() {
        return new String[] {"_olympiad"};
    }
}
