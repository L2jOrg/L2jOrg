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
package org.l2j.gameserver.model;

import org.l2j.gameserver.data.database.data.MacroCmdData;
import org.l2j.gameserver.data.database.data.MacroData;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.List;

/**
 * @author JoeAlisson
 */
public class Macro  {

    private final MacroData data;
    private final List<MacroCmdData> commands;

    public Macro(MacroData macroData, List<MacroCmdData> commands) {
        this.data = macroData;
        this.commands = commands;
    }

    public int getId() {
        return data.getId();
    }

    public int getIcon() {
        return data.getIcon();
    }

    public String getName() {
        return data.getName();
    }

    public String getDescription() {
        return data.getDescription();
    }

    public String getAcronym() {
        return data.getAcronym();
    }

    public List<MacroCmdData> getCommands() {
        return commands;
    }

    public MacroData getData() {
        return data;
    }

    public void updateId(int id) {
        data.setId(id);
        for (MacroCmdData command : commands) {
            command.setMacroId(id);
        }
    }

    public void updatePlayer(Player player) {
        data.setPlayerId(player.getObjectId());
        for (MacroCmdData command : commands) {
            command.setPlayerId(player.getObjectId());
        }
    }
}
