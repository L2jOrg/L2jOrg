/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.scripts.ai.others.DimensionalMerchant;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.engine.item.shop.MultisellEngine;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PackageToList;
import org.l2j.gameserver.network.serverpackets.WareHouseWithdrawalList;
import org.l2j.gameserver.network.serverpackets.attendance.ExVipAttendanceItemList;
import org.l2j.gameserver.network.serverpackets.html.ExPremiumManagerShowHtml;
import org.l2j.scripts.ai.AbstractNpcAI;

import java.util.StringTokenizer;

import static org.l2j.commons.util.Util.parseNextInt;

/**
 * @author JoeAlisson
 */
public class DimensionalMerchant extends AbstractNpcAI implements IBypassHandler {

    private DimensionalMerchant() {
        addFirstTalkId(32478);
        BypassHandler.getInstance().registerHandler(this);
    }

    @Override
    public String onFirstTalk(Npc npc, Player player) {
        openHtml(player, "index.htm");
        return null;
    }

    @Override
    public boolean useBypass(String bypass, Player player, Creature bypassOrigin) {
        var tokens = new StringTokenizer(bypass);
        tokens.nextToken(); // skip first
        if(tokens.hasMoreTokens()) {
            switch (tokens.nextToken()) {
                case "link" -> openHtml(player, tokens.nextToken());
                case "attendance_rewards" ->  player.sendPacket(new ExVipAttendanceItemList(player));
                case "shop" -> shop(player, parseNextInt(tokens, 0));
                case "market" -> player.sendMessage("There is no Dimensional Item"); // What is this supposed to do?
                case "package_deposit" -> packageDeposit(player);
                case "package_withdraw" -> packageWithdraw(player);
            }
            return true;
        }
        return false;
    }

    private void shop(Player player, int multiSell) {
        MultisellEngine.getInstance().separateAndSend(multiSell, player, null, false);
    }

    private void packageWithdraw(Player player) {
        var freight = player.getFreight();
        if (freight != null) {
            if (freight.getSize() > 0) {
                player.setActiveWarehouse(freight);
                for (var i : player.getActiveWarehouse().getItems()) {
                    if (i.isTimeLimitedItem() && (i.getRemainingTime() <= 0)) {
                        player.getActiveWarehouse().destroyItem("ItemInstance", i, player, null);
                    }
                }
                player.sendPacket(new WareHouseWithdrawalList(1, player, WareHouseWithdrawalList.FREIGHT));
                player.sendPacket(new WareHouseWithdrawalList(2, player, WareHouseWithdrawalList.FREIGHT));
            } else {
                player.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
            }
        }
    }

    private void packageDeposit(Player player) {
        if (player.getAccountChars().size() < 2) {
            player.sendPacket(SystemMessageId.THAT_CHARACTER_DOES_NOT_EXIST);
        } else {
            player.sendPacket(new PackageToList(player.getAccountChars()));
        }
    }

    public static void openHtml(Player player, String html) {

        ExPremiumManagerShowHtml html1 = new ExPremiumManagerShowHtml( HtmCache.getInstance().getHtm(player, "data/html/common/dimensional/" + html));

        var closeRange = player.getAutoPlayRangeClose();
        var longRange = player.getAutoPlayRangeLong();
        boolean viewRange = player.showAutoPlayRadius();
        boolean centrePoint = player.isAutoPlayZoneAnchored();

        if (closeRange == 0)
        {
            closeRange = Config.AUTO_PLAY_CLOSE_RANGE;
        }
        if (longRange == 0)
        {
            longRange = Config.AUTO_PLAY_LONG_RANGE;
        }

        html1.replace("%closeRange%", (closeRange <= 0 || closeRange > 800) ? String.valueOf(Config.AUTO_PLAY_CLOSE_RANGE) : String.valueOf(closeRange));
        html1.replace("%longRange%", (longRange < 800 || longRange > 2000) ? String.valueOf(Config.AUTO_PLAY_LONG_RANGE) : String.valueOf(longRange));
        html1.replace("%viewRange%", (viewRange ? "<font color=24a10e> ON </font>" : "<font color=ff0a0a> OFF </font>"));
        html1.replace("%centered%", (centrePoint ? "<font color=ff0a0a> CENTERED </font>" : "<font color=24a10e> DYNAMIC </font>"));
        html1.replace("ScloseRange", ("<button value=Set action="+"bypass _bbsautoplay closeRange $closeRange"+" width=85 height=26 back=L2UI_CT1.LCoinShopWnd.LCoinShopWnd_DF_Button fore=L2UI_CT1.LCoinShopWnd.LCoinShopWnd_DF_Button>"));
        html1.replace("SlongRange", ("<button value=Set action="+"bypass _bbsautoplay longRange $longRange"+" width=85 height=26 back=L2UI_CT1.LCoinShopWnd.LCoinShopWnd_DF_Button fore=L2UI_CT1.LCoinShopWnd.LCoinShopWnd_DF_Button>"));
        html1.replace("SviewRange", (viewRange ? "HIDE" : "SHOW"));
        html1.replace("Scentered", (centrePoint ? "Switch to Dynamic" : "Center it here"));

        player.sendPacket(html1);
    }

    @Override
    public String[] getBypassList() {
        return new String[] { "dimensional"};
    }

    public static DimensionalMerchant provider() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final DimensionalMerchant INSTANCE = new DimensionalMerchant();
    }
}
