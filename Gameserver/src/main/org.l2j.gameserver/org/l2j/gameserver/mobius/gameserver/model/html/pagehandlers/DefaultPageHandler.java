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
package org.l2j.gameserver.mobius.gameserver.model.html.pagehandlers;

import com.l2jmobius.gameserver.model.html.IBypassFormatter;
import com.l2jmobius.gameserver.model.html.IHtmlStyle;
import com.l2jmobius.gameserver.model.html.IPageHandler;

/**
 * Creates pager with links 1 2 3 | 9 10 | 998 | 999
 * @author UnAfraid
 */
public class DefaultPageHandler implements IPageHandler
{
	public static final DefaultPageHandler INSTANCE = new DefaultPageHandler(2);
	protected final int _pagesOffset;
	
	public DefaultPageHandler(int pagesOffset)
	{
		_pagesOffset = pagesOffset;
	}
	
	@Override
	public void apply(String bypass, int currentPage, int pages, StringBuilder sb, IBypassFormatter bypassFormatter, IHtmlStyle style)
	{
		final int pagerStart = Math.max(currentPage - _pagesOffset, 0);
		final int pagerFinish = Math.min(currentPage + _pagesOffset + 1, pages);
		
		// Show the initial pages in case we are in the middle or at the end
		if (pagerStart > _pagesOffset)
		{
			for (int i = 0; i < _pagesOffset; i++)
			{
				sb.append(style.applyBypass(bypassFormatter.formatBypass(bypass, i), String.valueOf(i + 1), currentPage == i));
			}
			
			// Separator
			sb.append(style.applySeparator());
		}
		
		// Show current pages
		for (int i = pagerStart; i < pagerFinish; i++)
		{
			sb.append(style.applyBypass(bypassFormatter.formatBypass(bypass, i), String.valueOf(i + 1), currentPage == i));
		}
		
		// Show the last pages
		if (pages > pagerFinish)
		{
			// Separator
			sb.append(style.applySeparator());
			
			for (int i = pages - _pagesOffset; i < pages; i++)
			{
				sb.append(style.applyBypass(bypassFormatter.formatBypass(bypass, i), String.valueOf(i + 1), currentPage == i));
			}
		}
	}
}