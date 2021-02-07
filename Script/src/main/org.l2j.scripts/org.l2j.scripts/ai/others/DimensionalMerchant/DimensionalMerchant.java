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
package org.l2j.scripts.ai.others.DimensionalMerchant;

import org.l2j.gameserver.engine.item.shop.MultisellEngine;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PackageToList;
import org.l2j.gameserver.network.serverpackets.WareHouseWithdrawalList;
import org.l2j.gameserver.network.serverpackets.attendance.ExVipAttendanceItemList;
import org.l2j.scripts.ai.AbstractNpcAI;

/**
 * Dimensional Merchant AI.
 * @author Mobius
 */
public class DimensionalMerchant extends AbstractNpcAI
{
    // Others
    private static final int ATTENDANCE_REWARD_MULTISELL = 3247801;
    private static final int HAIR_MULTISELL = 4706;
    private static final int TICKET_MULTISELL = 4707;
    private static final int ENHANCEMENT_MULTISELL = 567;
    private static final int CLOAK_MULTISELL = 4669;
    private static final int HERO_MULTISELL = 4680;
    private static final int WEAPON_D_MULTISELL = 4037;
    private static final int WEAPON_C_MULTISELL = 4038;
    private static final String COMMAND_BYPASS = "Quest DimensionalMerchant ";

    private DimensionalMerchant()
    {
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player)
    {
        String htmltext = null;
        switch (event)
        {
            case "package_deposit":
            {
                if (player.getAccountChars().size() < 1)
                {
                    player.sendPacket(SystemMessageId.THAT_CHARACTER_DOES_NOT_EXIST);
                }
                else
                {
                    player.sendPacket(new PackageToList(player.getAccountChars()));
                }
                break;
            }
            case "package_withdraw":
            {
                var freight = player.getFreight();
                if (freight != null)
                {
                    if (freight.getSize() > 0)
                    {
                        player.setActiveWarehouse(freight);
                        for (var i : player.getActiveWarehouse().getItems())
                        {
                            if (i.isTimeLimitedItem() && (i.getRemainingTime() <= 0))
                            {
                                player.getActiveWarehouse().destroyItem("ItemInstance", i, player, null);
                            }
                        }
                        player.sendPacket(new WareHouseWithdrawalList(1, player, WareHouseWithdrawalList.FREIGHT));
                        player.sendPacket(new WareHouseWithdrawalList(2, player, WareHouseWithdrawalList.FREIGHT));
                    }
                    else
                    {
                        player.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
                    }
                }
                break;
            }
            case "attendance_rewards":
            {
                player.sendPacket(new ExVipAttendanceItemList(player));
                break;
            }
            case "shop":
            {
                MultisellEngine.getInstance().separateAndSend(ATTENDANCE_REWARD_MULTISELL, player, null, false);
                break;
            }
            case "hair":{
                MultisellEngine.getInstance().separateAndSend(HAIR_MULTISELL, player, null, false);
                break;
            }
            case "ticket":{
                MultisellEngine.getInstance().separateAndSend(TICKET_MULTISELL, player, null, false);
                break;
            }
            case "enhancement":{
                MultisellEngine.getInstance().separateAndSend(ENHANCEMENT_MULTISELL, player, null, false);
                break;
            }
            case "kingdomcloak":{
                MultisellEngine.getInstance().separateAndSend(CLOAK_MULTISELL, player, null, false);
                break;
            }
            case "herosweapon":{
                MultisellEngine.getInstance().separateAndSend(HERO_MULTISELL, player, null, false);
                break;
            }
            case "timedweapond":{
                MultisellEngine.getInstance().separateAndSend(WEAPON_D_MULTISELL, player, null, false);
                break;
            }
            case "timedweaponc":{
                MultisellEngine.getInstance().separateAndSend(WEAPON_C_MULTISELL, player, null, false);
                break;
            }

        }
        return htmltext;
    }

    @RegisterEvent(EventType.ON_PLAYER_BYPASS)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerBypass(OnPlayerBypass event)
    {
        var player = event.getPlayer();
        if (event.getCommand().startsWith(COMMAND_BYPASS))
        {
            notifyEvent(event.getCommand().replace(COMMAND_BYPASS, ""), null, player);
        }
    }

    public static DimensionalMerchant provider() {
        return new DimensionalMerchant();
    }
}
