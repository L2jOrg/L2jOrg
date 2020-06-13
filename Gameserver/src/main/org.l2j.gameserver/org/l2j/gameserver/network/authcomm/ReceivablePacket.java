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
package org.l2j.gameserver.network.authcomm;

import io.github.joealisson.mmocore.ReadablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ReceivablePacket extends ReadablePacket<AuthServerClient> {
	private static final Logger logger = LoggerFactory.getLogger(ReceivablePacket.class);

	@Override
	public final boolean read() {
		try {
			readImpl();
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return false;
		}
		return true;
	}

	@Override
	public final void run() {
		try {
			runImpl();
		} catch(Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	protected abstract void readImpl();

	protected abstract void runImpl();

	protected void sendPacket(SendablePacket sp)
	{
		client.sendPacket(sp);
	}
}
