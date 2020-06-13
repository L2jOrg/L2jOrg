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
package org.l2j.gameserver.model.html;

import org.l2j.gameserver.model.html.formatters.DefaultFormatter;
import org.l2j.gameserver.model.html.pagehandlers.DefaultPageHandler;
import org.l2j.gameserver.model.html.styles.DefaultStyle;

import java.util.Arrays;
import java.util.Collection;

import static java.lang.Math.max;
import static java.util.Objects.requireNonNull;

/**
 * @param <T>
 * @author UnAfraid
 */
public class PageBuilder<T> {
    private final Collection<T> elements;
    private final int elementsPerPage;
    private final String bypass;
    private int currentPage = 0;
    private IPageHandler pageHandler = DefaultPageHandler.INSTANCE;
    private IBypassFormatter formatter = DefaultFormatter.INSTANCE;
    private IHtmlStyle style = DefaultStyle.INSTANCE;
    private IBodyHandler<T> bodyHandler;

    private PageBuilder(Collection<T> elements, int elementsPerPage, String bypass) {
        this.elements = elements;
        this.elementsPerPage = elementsPerPage;
        this.bypass = bypass;
    }

    public static <T> PageBuilder<T> newBuilder(Collection<T> elements, int elementsPerPage, String bypass) {
        return new PageBuilder<>(elements, elementsPerPage, bypass.trim());
    }

    public static <T> PageBuilder<T> newBuilder(T[] elements, int elementsPerPage, String bypass) {
        return new PageBuilder<>(Arrays.asList(elements), elementsPerPage, bypass.trim());
    }

    public PageBuilder<T> currentPage(int currentPage) {
        this.currentPage = max(currentPage, 0);
        return this;
    }

    public PageBuilder<T> bodyHandler(IBodyHandler<T> bodyHandler) {
        requireNonNull(bodyHandler, "Body Handler cannot be null!");
        this.bodyHandler = bodyHandler;
        return this;
    }

    public PageBuilder<T> pageHandler(IPageHandler pageHandler) {
        requireNonNull(pageHandler, "Page Handler cannot be null!");
        this.pageHandler = pageHandler;
        return this;
    }

    public PageBuilder<T> formatter(IBypassFormatter formatter) {
        requireNonNull(formatter, "Formatter cannot be null!");
        this.formatter = formatter;
        return this;
    }

    public PageBuilder<T> style(IHtmlStyle style) {
        requireNonNull(style, "Style cannot be null!");
        this.style = style;
        return this;
    }

    public PageResult build() {
        requireNonNull(bodyHandler, "Body was not set!");

        final int pages = (elements.size() / elementsPerPage) + ((elements.size() % elementsPerPage) > 0 ? 1 : 0);
        final StringBuilder pagerTemplate = new StringBuilder();
        if (pages > 1) {
            pageHandler.apply(bypass, currentPage, pages, pagerTemplate, formatter, style);
        }

        if (currentPage > pages) {
            currentPage = pages - 1;
        }

        final int start = max(elementsPerPage * currentPage, 0);
        final StringBuilder sb = new StringBuilder();
        bodyHandler.create(elements, pages, start, elementsPerPage, sb);
        return new PageResult(pages, pagerTemplate, sb);
    }
}
