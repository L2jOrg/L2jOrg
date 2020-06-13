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
package org.l2j.gameserver.model.html.styles;

import org.l2j.gameserver.model.html.IHtmlStyle;

/**
 * @author UnAfraid
 */
public class DefaultStyle implements IHtmlStyle {
    public static final DefaultStyle INSTANCE = new DefaultStyle();
    private static final String DEFAULT_PAGE_LINK_FORMAT = "<td><a action=\"%s\">%s</a></td>";
    private static final String DEFAULT_PAGE_TEXT_FORMAT = "<td>%s</td>";
    private static final String DEFAULT_PAGER_SEPARATOR = "<td align=center> | </td>";

    @Override
    public String applyBypass(String bypass, String name, boolean isActive) {
        if (isActive) {
            return String.format(DEFAULT_PAGE_TEXT_FORMAT, name);
        }
        return String.format(DEFAULT_PAGE_LINK_FORMAT, bypass, name);
    }

    @Override
    public String applySeparator() {
        return DEFAULT_PAGER_SEPARATOR;
    }
}
