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
package org.l2j.gameserver.network.clientpackets.pledge.bonus;

import org.l2j.gameserver.enums.ClanRewardType;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

/**
 * @author UnAfraid
 */
public class RequestPledgeBonusReward extends ClientPacket {

    private int _type;

    @Override
    public void readImpl() {
        _type = readByte();
    }

    @Override
    public void runImpl() {
        var player = client.getPlayer();
        if ((player == null) || (player.getClan() == null)) {
            return;
        }

        if ((_type < 0) || (_type > ClanRewardType.values().length)) {
            return;
        }

        final Clan clan = player.getClan();
        final ClanRewardType type = ClanRewardType.values()[_type];

        if (clan.canClaimBonusReward(player, type)) {
            var bonus = type.getAvailableBonus(player.getClan());
            var itemReward = bonus.getItemReward();
            var skillReward = bonus.getSkillReward();

            if (itemReward != null) {
                player.addItem("ClanReward", itemReward.getId(), itemReward.getCount(), player, true);
            } else if (skillReward != null) {
                skillReward.getSkill().activateSkill(player, player);
            }

            int claimedRewards = player.getClaimedClanRewards(ClanRewardType.getDefaultMask());
            claimedRewards |= type.getMask();
            player.setClaimedClanRewards(claimedRewards);
            player.storeVariables();
        }
    }
}
