package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.enums.HtmlActionScope;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.util.Util;

/**
 * @author HorridoJoho
 */
public abstract class AbstractHtmlPacket extends IClientOutgoingPacket {
    public static final char VAR_PARAM_START_CHAR = '$';

    private final int _npcObjId;
    private String _html = null;
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

    public final boolean setFile(L2PcInstance player, String path) {
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
        _html = _html.replaceAll(pattern, value.replaceAll("\\$", "\\\\\\$"));
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
    public final void runImpl(L2PcInstance player) {
        if (player != null) {
            player.clearHtmlActions(getScope());
        }

        if (_disabledValidation) {
            return;
        }

        if (player != null) {
            Util.buildHtmlActionCache(player, getScope(), _npcObjId, _html);
        }
    }

    public final int getNpcObjId() {
        return _npcObjId;
    }

    public final String getHtml() {
        return _html;
    }

    public final void setHtml(String html) {
        if (html.length() > 17200) {
            LOGGER.warn("Html is too long! this will crash the client!", new Throwable());
            _html = html.substring(0, 17200);
        }

        if (!html.contains("<html") && !html.startsWith("..\\L2")) {
            html = "<html><body>" + html + "</body></html>";
        }

        _html = html;
    }

    public abstract HtmlActionScope getScope();
}
