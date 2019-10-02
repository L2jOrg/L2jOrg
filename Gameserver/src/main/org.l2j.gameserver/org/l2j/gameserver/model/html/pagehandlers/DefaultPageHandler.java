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