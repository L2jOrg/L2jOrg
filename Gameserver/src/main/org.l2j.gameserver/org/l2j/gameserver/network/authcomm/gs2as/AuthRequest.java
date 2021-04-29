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
package org.l2j.gameserver.network.authcomm.gs2as;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.settings.ServerSettings;

public class AuthRequest extends SendablePacket {

	protected void writeImpl(AuthServerClient client, WritableBuffer buffer) {
		buffer.writeByte(0x00);
		buffer.writeByte(ServerSettings.serverId());
		buffer.writeByte(ServerSettings.acceptAlternativeId());
		buffer.writeInt(ServerSettings.type());
		buffer.writeInt(ServerSettings.maximumOnlineUsers());
		buffer.writeByte(ServerSettings.ageLimit());

		buffer.writeByte(ServerSettings.isShowingBrackets());
		buffer.writeByte(ServerSettings.isPvP());

		var hosts = Config.GAME_SERVER_HOSTS.size();
		buffer.writeShort(hosts);

		for (int i = 0; i < Config.GAME_SERVER_HOSTS.size(); i++) {
			buffer.writeString(Config.GAME_SERVER_HOSTS.get(i));
			buffer.writeString(Config.GAME_SERVER_SUBNETS.get(i));
		}
		buffer.writeShort(ServerSettings.port());
	}
}