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
