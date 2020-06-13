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
package org.l2j.gameserver.model.html.pagehandlers;

import org.l2j.gameserver.model.html.IBypassFormatter;
import org.l2j.gameserver.model.html.IHtmlStyle;
import org.l2j.gameserver.model.html.IPageHandler;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Creates pager with links 1 2 3 | 9 10 | 998 | 999
 *
 * @author UnAfraid
 */
public class DefaultPageHandler implements IPageHandler {
    public static final DefaultPageHandler INSTANCE = new DefaultPageHandler(2);
    protected final int pagesOffset;

    public DefaultPageHandler(int pagesOffset) {
        this.pagesOffset = pagesOffset;
    }

    @Override
    public void apply(String bypass, int currentPage, int pages, StringBuilder sb, IBypassFormatter formatter, IHtmlStyle style) {
        final int pagerStart = max(currentPage - pagesOffset, 0);
        final int pagerFinish = min(currentPage + pagesOffset + 1, pages);

        // Show the initial pages in case we are in the middle or at the end
        if (pagerStart > pagesOffset) {
            for (int i = 0; i < pagesOffset; i++) {
                sb.append(style.applyBypass(formatter.formatBypass(bypass, i), String.valueOf(i + 1), currentPage == i));
            }

            // Separator
            sb.append(style.applySeparator());
        }

        // Show current pages
        for (int i = pagerStart; i < pagerFinish; i++) {
            sb.append(style.applyBypass(formatter.formatBypass(bypass, i), String.valueOf(i + 1), currentPage == i));
        }

        // Show the last pages
        if (pages > pagerFinish) {
            // Separator
            sb.append(style.applySeparator());

            for (int i = pages - pagesOffset; i < pages; i++) {
                sb.append(style.applyBypass(formatter.formatBypass(bypass, i), String.valueOf(i + 1), currentPage == i));
            }
        }
    }
}