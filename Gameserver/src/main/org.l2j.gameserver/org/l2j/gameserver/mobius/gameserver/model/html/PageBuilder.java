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
package org.l2j.gameserver.mobius.gameserver.model.html;

import com.l2jmobius.gameserver.model.html.formatters.DefaultFormatter;
import com.l2jmobius.gameserver.model.html.pagehandlers.DefaultPageHandler;
import com.l2jmobius.gameserver.model.html.styles.DefaultStyle;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * @author UnAfraid
 * @param <T>
 */
public class PageBuilder<T>
{
	private final Collection<T> _elements;
	private final int _elementsPerPage;
	private final String _bypass;
	private int _currentPage = 0;
	private IPageHandler _pageHandler = DefaultPageHandler.INSTANCE;
	private IBypassFormatter _formatter = DefaultFormatter.INSTANCE;
	private IHtmlStyle _style = DefaultStyle.INSTANCE;
	private IBodyHandler<T> _bodyHandler;
	
	private PageBuilder(Collection<T> elements, int elementsPerPage, String bypass)
	{
		_elements = elements;
		_elementsPerPage = elementsPerPage;
		_bypass = bypass;
	}
	
	public PageBuilder<T> currentPage(int currentPage)
	{
		_currentPage = Math.max(currentPage, 0);
		return this;
	}
	
	public PageBuilder<T> bodyHandler(IBodyHandler<T> bodyHandler)
	{
		Objects.requireNonNull(bodyHandler, "Body Handler cannot be null!");
		_bodyHandler = bodyHandler;
		return this;
	}
	
	public PageBuilder<T> pageHandler(IPageHandler pageHandler)
	{
		Objects.requireNonNull(pageHandler, "Page Handler cannot be null!");
		_pageHandler = pageHandler;
		return this;
	}
	
	public PageBuilder<T> formatter(IBypassFormatter formatter)
	{
		Objects.requireNonNull(formatter, "Formatter cannot be null!");
		_formatter = formatter;
		return this;
	}
	
	public PageBuilder<T> style(IHtmlStyle style)
	{
		Objects.requireNonNull(style, "Style cannot be null!");
		_style = style;
		return this;
	}
	
	public PageResult build()
	{
		Objects.requireNonNull(_bodyHandler, "Body was not set!");
		
		final int pages = (_elements.size() / _elementsPerPage) + ((_elements.size() % _elementsPerPage) > 0 ? 1 : 0);
		final StringBuilder pagerTemplate = new StringBuilder();
		if (pages > 1)
		{
			_pageHandler.apply(_bypass, _currentPage, pages, pagerTemplate, _formatter, _style);
		}
		
		if (_currentPage > pages)
		{
			_currentPage = pages - 1;
		}
		
		final int start = Math.max(_elementsPerPage * _currentPage, 0);
		final StringBuilder sb = new StringBuilder();
		_bodyHandler.create(_elements, pages, start, _elementsPerPage, sb);
		return new PageResult(pages, pagerTemplate, sb);
	}
	
	public static <T> PageBuilder<T> newBuilder(Collection<T> elements, int elementsPerPage, String bypass)
	{
		return new PageBuilder<>(elements, elementsPerPage, bypass.trim());
	}
	
	public static <T> PageBuilder<T> newBuilder(T[] elements, int elementsPerPage, String bypass)
	{
		return new PageBuilder<>(Arrays.asList(elements), elementsPerPage, bypass.trim());
	}
}
