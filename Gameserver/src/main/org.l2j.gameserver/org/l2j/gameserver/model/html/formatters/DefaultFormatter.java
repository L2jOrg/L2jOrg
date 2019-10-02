package org.l2j.gameserver.model.html.formatters;

import org.l2j.gameserver.model.html.IBypassFormatter;

import static org.l2j.commons.util.Util.SPACE;

/**
 * @author UnAfraid
 */
public class DefaultFormatter implements IBypassFormatter {
    public static final DefaultFormatter INSTANCE = new DefaultFormatter();

    @Override
    public String formatBypass(String bypass, int page) {
        return bypass + SPACE + page;
    }
}
