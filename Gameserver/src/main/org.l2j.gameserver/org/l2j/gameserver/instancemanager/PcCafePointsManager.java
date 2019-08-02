package org.l2j.gameserver.instancemanager;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.ZoneId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExPCCafePointInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

public final class PcCafePointsManager {

    private PcCafePointsManager() {

    }

    public void givePcCafePoint(Player player, double exp) {
        if (!Config.PC_CAFE_ENABLED || player.isInsideZone(ZoneId.PEACE) || player.isInsideZone(ZoneId.PVP) || player.isInsideZone(ZoneId.SIEGE) || (player.isOnlineInt() == 0) || player.isJailed()) {
            return;
        }

        // PC-points only premium accounts
        if (Config.PC_CAFE_ONLY_VIP && player.getVipTier() <= 0) {
            return;
        }

        if (player.getPcCafePoints() >= Config.PC_CAFE_MAX_POINTS) {
            final SystemMessage message = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_THE_MAXIMUM_NUMBER_OF_PA_POINTS);
            player.sendPacket(message);
            return;
        }

        int points = (int) (exp * 0.0001 * Config.PC_CAFE_POINT_RATE);

        if (Config.PC_CAFE_RANDOM_POINT) {
            points = Rnd.get(points / 2, points);
        }

        if ((points == 0) && (exp > 0) && Config.PC_CAFE_REWARD_LOW_EXP_KILLS && (Rnd.get(100) < Config.PC_CAFE_LOW_EXP_KILLS_CHANCE)) {
            points = 1; // minimum points
        }

        if (points <= 0) {
            return;
        }

        SystemMessage message;
        if (Config.PC_CAFE_ENABLE_DOUBLE_POINTS && (Rnd.get(100) < Config.PC_CAFE_DOUBLE_POINTS_CHANCE)) {
            points *= 2;
            message = SystemMessage.getSystemMessage(SystemMessageId.DOUBLE_POINTS_YOU_EARNED_S1_PA_POINT_S);
        } else {
            message = SystemMessage.getSystemMessage(SystemMessageId.DOUBLE_POINTS_YOU_EARNED_S1_PA_POINT_S);
        }
        if ((player.getPcCafePoints() + points) > Config.PC_CAFE_MAX_POINTS) {
            points = Config.PC_CAFE_MAX_POINTS - player.getPcCafePoints();
        }
        message.addLong(points);
        player.sendPacket(message);
        player.setPcCafePoints(player.getPcCafePoints() + points);
        player.sendPacket(new ExPCCafePointInfo(player.getPcCafePoints(), points, 1));
    }

    public static PcCafePointsManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PcCafePointsManager INSTANCE = new PcCafePointsManager();
    }
}