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
package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.enums.MacroType;

/**
 * @author JoeAlisson
 */
@Table("macro_commands")
public class MacroCmdData {

    @Column("macro_id")
    private int macroId;
    @Column("macro_player_id")
    private int playerId;
    private int index;
    private MacroType type;
    private int data1;
    private int data2;
    private String command;

    public int getMacroId() {
        return macroId;
    }

    public void setMacroId(int macroId) {
        this.macroId = macroId;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public MacroType getType() {
        return type;
    }

    public void setType(MacroType type) {
        this.type = type;
    }

    public int getData1() {
        return data1;
    }

    public void setData1(int data1) {
        this.data1 = data1;
    }

    public int getData2() {
        return data2;
    }

    public void setData2(int data2) {
        this.data2 = data2;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
