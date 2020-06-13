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
package org.l2j.gameserver.data.xml.model;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class ActionData {
    private final int id;
    private final String handler;
    private final int optionId;
    private final boolean autoUse;

    public ActionData(Integer id, String handler, Integer optionId, boolean autoUse) {
        this.id = id;
        this.handler = handler;
        this.optionId = optionId;
        this.autoUse = autoUse;
    }

    public int getId() {
        return id;
    }

    public String getHandler() {
        return handler;
    }

    public int getOptionId() {
        return optionId;
    }

    public boolean isAutoUse() {
        return autoUse;
    }
}
