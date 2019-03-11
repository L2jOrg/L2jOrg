/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.zone.type;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.zone.L2ZoneType;
import org.l2j.gameserver.model.zone.ZoneId;

/**
 * A scripted zone... Creation of such a zone should require somekind of script reference which can handle onEnter() / onExit()
 *
 * @author durgus
 */
public class L2ScriptZone extends L2ZoneType {
    public L2ScriptZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(L2Character character) {
        character.setInsideZone(ZoneId.SCRIPT, true);
    }

    @Override
    protected void onExit(L2Character character) {
        character.setInsideZone(ZoneId.SCRIPT, false);
    }
}
