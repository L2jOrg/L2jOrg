package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.settings.ServerSettings;

public class AuthRequest extends SendablePacket {

	protected void writeImpl() {
		var serverSettings = Configurator.getSettings(ServerSettings.class);
		writeByte(0x00);
		writeByte(serverSettings.serverId());
		writeByte(0x00); // ACCEPT ALTERNATE ID
		writeString(serverSettings.internalAddress());
		writeString(serverSettings.externalAddress());
		writeShort(serverSettings.port());
		writeInt(serverSettings.type());
		writeByte(serverSettings.ageLimit());
		writeByte(serverSettings.isGMOnly());
		writeByte(serverSettings.isShowingBrackets());
		writeByte(serverSettings.isPvP());
		writeInt(serverSettings.maximumOnlineUsers());
	}
}