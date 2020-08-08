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
package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.model.punishment.PunishmentTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.network.serverpackets.ExUserBanInfo;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class ExRequestUserBanInfo extends ClientPacket {

    private int playerId;

    @Override
    protected void readImpl() throws Exception {
        playerId = readInt();
    }

    @Override
    protected void runImpl()  {
        int accessLevel = client.getPlayerInfoAccessLevel(playerId);
        if(accessLevel < 0) {
            client.sendPacket(new ExUserBanInfo(PunishmentType.PERMANENT_BAN, 0, "violation of the terms of conduct"));
        } else {
            PunishmentTask punishment = PunishmentManager.getInstance().getPunishment(playerId, PunishmentAffect.CHARACTER, PunishmentType.BAN);
            if(nonNull(punishment) && punishment.getExpirationTime() > 0) {
                client.sendPacket(new ExUserBanInfo(PunishmentType.BAN, punishment.getExpirationTime(), punishment.getReason()));
            }
        }
    }
}
