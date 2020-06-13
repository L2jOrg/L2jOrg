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
package org.l2j.gameserver.network.serverpackets.appearance;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ceremonyofchaos.CeremonyOfChaosMember;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
public class ExCuriousHouseMemberUpdate extends ServerPacket {
    public final int _objId;
    public final int _maxHp;
    public final int _maxCp;
    public final int _currentHp;
    public final int _currentCp;

    public ExCuriousHouseMemberUpdate(CeremonyOfChaosMember member) {
        _objId = member.getObjectId();
        final Player player = member.getPlayer();
        if (player != null) {
            _maxHp = player.getMaxHp();
            _maxCp = player.getMaxCp();
            _currentHp = (int) player.getCurrentHp();
            _currentCp = (int) player.getCurrentCp();
        } else {
            _maxHp = 0;
            _maxCp = 0;
            _currentHp = 0;
            _currentCp = 0;
        }
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CURIOUS_HOUSE_MEMBER_UPDATE);

        writeInt(_objId);
        writeInt(_maxHp);
        writeInt(_maxCp);
        writeInt(_currentHp);
        writeInt(_currentCp);
    }

}
