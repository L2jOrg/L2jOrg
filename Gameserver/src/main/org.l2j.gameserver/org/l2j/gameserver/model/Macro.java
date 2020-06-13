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

import org.l2j.gameserver.model.interfaces.IIdentifiable;
import org.l2j.gameserver.model.interfaces.INamable;

import java.util.List;

public class Macro implements IIdentifiable, INamable {
    private final int _icon;
    private final String _name;
    private final String _descr;
    private final String _acronym;
    private final List<MacroCmd> _commands;
    private int _id;

    /**
     * Constructor for macros.
     *
     * @param id      the macro ID
     * @param icon    the icon ID
     * @param name    the macro name
     * @param descr   the macro description
     * @param acronym the macro acronym
     * @param list    the macro command list
     */
    public Macro(int id, int icon, String name, String descr, String acronym, List<MacroCmd> list) {
        _id = id;
        _icon = icon;
        _name = name;
        _descr = descr;
        _acronym = acronym;
        _commands = list;
    }

    /**
     * Gets the marco ID.
     *
     * @returns the marco ID
     */
    @Override
    public int getId() {
        return _id;
    }

    /**
     * Sets the marco ID.
     *
     * @param id the marco ID
     */
    public void setId(int id) {
        _id = id;
    }

    /**
     * Gets the macro icon ID.
     *
     * @return the icon
     */
    public int getIcon() {
        return _icon;
    }

    /**
     * Gets the macro name.
     *
     * @return the name
     */
    @Override
    public String getName() {
        return _name;
    }

    /**
     * Gets the macro description.
     *
     * @return the description
     */
    public String getDescr() {
        return _descr;
    }

    /**
     * Gets the macro acronym.
     *
     * @return the acronym
     */
    public String getAcronym() {
        return _acronym;
    }

    /**
     * Gets the macro command list.
     *
     * @return the macro command list
     */
    public List<MacroCmd> getCommands() {
        return _commands;
    }
}
