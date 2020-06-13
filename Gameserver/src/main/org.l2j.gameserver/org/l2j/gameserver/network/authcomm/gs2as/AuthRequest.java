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
package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.settings.ServerSettings;

public class AuthRequest extends SendablePacket {

	protected void writeImpl(AuthServerClient client) {
		var serverSettings = Configurator.getSettings(ServerSettings.class);
		writeByte((byte) 0x00);
		writeByte((byte) serverSettings.serverId());
		writeByte((byte) (serverSettings.acceptAlternativeId() ? 0x01 : 0x00));
		writeInt(serverSettings.type());
		writeInt(serverSettings.maximumOnlineUsers());
		writeByte(serverSettings.ageLimit());

		writeByte((byte) (serverSettings.isShowingBrackets() ? 0x01 : 0x00));
		writeByte((byte) (serverSettings.isPvP() ? 0x01 : 0x00));

		var hosts = Config.GAME_SERVER_HOSTS.size();
		writeShort((short) hosts);

		for (int i = 0; i < Config.GAME_SERVER_HOSTS.size(); i++) {
			writeString(Config.GAME_SERVER_HOSTS.get(i));
			writeString(Config.GAME_SERVER_SUBNETS.get(i));
		}
		writeShort(serverSettings.port());
	}
}