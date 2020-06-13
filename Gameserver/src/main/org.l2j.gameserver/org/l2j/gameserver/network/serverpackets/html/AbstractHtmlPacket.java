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
package org.l2j.gameserver.network.serverpackets.html;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HorridoJoho
 */
public abstract class AbstractHtmlPacket extends ServerPacket {
    public static final char VAR_PARAM_START_CHAR = '$';
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHtmlPacket.class);

    private final int _npcObjId;
    private String html = null;
    private boolean _disabledValidation = false;

    protected AbstractHtmlPacket() {
        _npcObjId = 0;
    }

    protected AbstractHtmlPacket(int npcObjId) {
        if (npcObjId < 0) {
            throw new IllegalArgumentException();
        }

        _npcObjId = npcObjId;
    }

    protected AbstractHtmlPacket(String html) {
        _npcObjId = 0;
        setHtml(html);
    }

    protected AbstractHtmlPacket(int npcObjId, String html) {
        if (npcObjId < 0) {
            throw new IllegalArgumentException();
        }

        _npcObjId = npcObjId;
        setHtml(html);
    }

    public final void disableValidation() {
        _disabledValidation = true;
    }

    public final boolean setFile(Player player, String path) {
        final String content = HtmCache.getInstance().getHtm(player, path);
        if (content == null) {
            setHtml("<html><body>My Text is missing:<br>" + path + "</body></html>");
            LOGGER.warn("missing html page " + path);
            return false;
        }

        setHtml(content);
        return true;
    }

    public final void replace(String pattern, String value) {
        html = html.replaceAll(pattern, value.replaceAll("\\$", "\\\\\\$"));
    }

    public final void replace(String pattern, CharSequence value) {
        replace(pattern, String.valueOf(value));
    }

    public final void replace(String pattern, boolean val) {
        replace(pattern, String.valueOf(val));
    }

    public final void replace(String pattern, int val) {
        replace(pattern, String.valueOf(val));
    }

    public final void replace(String pattern, long val) {
        replace(pattern, String.valueOf(val));
    }

    public final void replace(String pattern, double val) {
        replace(pattern, String.valueOf(val));
    }

    @Override
    public final void runImpl(Player player) {
        if (player != null) {
            player.clearHtmlActions(getScope());
        }

        if (_disabledValidation) {
            return;
        }

        if (player != null) {
            GameUtils.buildHtmlActionCache(player, getScope(), _npcObjId, html);
        }
    }

    public final int getNpcObjId() {
        return _npcObjId;
    }

    public final String getHtml() {
        return html;
    }

    public final void setHtml(String html) {
        if (html.length() > 17200) {
            LOGGER.warn("Html is too long! this will crash the client!", new Throwable());
            this.html = html.substring(0, 17200);
        }

        if (!html.contains("<html") && !html.startsWith("..\\L2")) {
            html = "<html><body>" + html + "</body></html>";
        }

        this.html = html;
    }

    public abstract HtmlActionScope getScope();
}
