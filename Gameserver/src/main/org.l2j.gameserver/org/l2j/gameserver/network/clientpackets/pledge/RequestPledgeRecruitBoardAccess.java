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
package org.l2j.gameserver.network.clientpackets.pledge;

import org.l2j.gameserver.data.database.data.PledgeRecruitData;
import org.l2j.gameserver.instancemanager.ClanEntryManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * @author Sdw
 */
public class RequestPledgeRecruitBoardAccess extends ClientPacket {
    private int _applyType;
    private int _karma;
    private String _information;
    private String _datailedInformation;
    private int _applicationType;
    private int _recruitingType;

    @Override
    public void readImpl() {
        _applyType = readInt();
        _karma = readInt();
        _information = readString();
        _datailedInformation = readString();
        _applicationType = readInt(); // 0 - Allow, 1 - Public
        _recruitingType = readInt(); // 0 - Main clan
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();

        if (player == null) {
            return;
        }

        final Clan clan = player.getClan();

        if (clan == null) {
            player.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN);
            return;
        }

        if (!player.hasClanPrivilege(ClanPrivilege.CL_MANAGE_RANKS)) {
            player.sendPacket(SystemMessageId.ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN);
            return;
        }

        final PledgeRecruitData pledgeRecruitInfo = new PledgeRecruitData(clan.getId(), _karma, _information, _datailedInformation, _applicationType, _recruitingType);

        switch (_applyType) {
            case 0: // remove
            {
                ClanEntryManager.getInstance().removeFromClanList(clan.getId());
                break;
            }
            case 1: // add
            {
                if (ClanEntryManager.getInstance().addToClanList(clan.getId(), pledgeRecruitInfo)) {
                    player.sendPacket(SystemMessageId.ENTRY_APPLICATION_COMPLETE_USE_ENTRY_APPLICATION_INFO_TO_CHECK_OR_CANCEL_YOUR_APPLICATION_APPLICATION_IS_AUTOMATICALLY_CANCELLED_AFTER_30_DAYS_IF_YOU_CANCEL_APPLICATION_YOU_CANNOT_APPLY_AGAIN_FOR_5_MINUTES);
                } else {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
                    sm.addLong(ClanEntryManager.getInstance().getClanLockTime(clan.getId()));
                    player.sendPacket(sm);
                }
                break;
            }
            case 2: // update
            {
                if (ClanEntryManager.getInstance().updateClanList(clan.getId(), pledgeRecruitInfo)) {
                    player.sendPacket(SystemMessageId.ENTRY_APPLICATION_COMPLETE_USE_ENTRY_APPLICATION_INFO_TO_CHECK_OR_CANCEL_YOUR_APPLICATION_APPLICATION_IS_AUTOMATICALLY_CANCELLED_AFTER_30_DAYS_IF_YOU_CANCEL_APPLICATION_YOU_CANNOT_APPLY_AGAIN_FOR_5_MINUTES);
                } else {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION);
                    sm.addLong(ClanEntryManager.getInstance().getClanLockTime(clan.getId()));
                    player.sendPacket(sm);
                }
                break;
            }
        }
    }

}
