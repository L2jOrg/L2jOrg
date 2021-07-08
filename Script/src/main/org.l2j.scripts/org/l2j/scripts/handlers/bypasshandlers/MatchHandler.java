package org.l2j.scripts.handlers.bypasshandlers;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;

import java.util.StringTokenizer;

/**
 * @author JoeAlisson
 */
public class MatchHandler implements IBypassHandler {

    @Override
    public boolean useBypass(String bypass, Player player, Creature bypassOrigin) {
        bypass = bypass.substring(bypass.indexOf("=") + 1).trim();
        final var tokens = new StringTokenizer(bypass, "&");
        final var classId = Util.parseNextInt(tokens, 0);
        final var page = Util.parseNextInt(tokens, 1);
        Olympiad.getInstance().showHeroHistory(player, classId, page);
        return true;
    }

    @Override
    public String[] getBypassList() {
        return new String[] { "_match" };
    }
}
