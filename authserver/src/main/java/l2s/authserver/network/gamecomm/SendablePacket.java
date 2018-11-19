/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package l2s.authserver.network.gamecomm;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class SendablePacket extends l2s.commons.net.nio.SendablePacket<GameServer>
{
	private static final Logger _log = LoggerFactory.getLogger(SendablePacket.class);

	protected GameServer _gs;
	protected ByteBuffer _buf;

	protected void setByteBuffer(ByteBuffer buf)
	{
		_buf = buf;
	}

	@Override
	protected ByteBuffer getByteBuffer()
	{
		return _buf;
	}

	protected void setClient(GameServer gs)
	{
		_gs = gs;
	}

	@Override
	public GameServer getClient()
	{
		return _gs;
	}

	public GameServer getGameServer()
	{
		return getClient();
	}

	@Override
	public boolean write()
	{
		try
		{
			writeImpl();
		}
		catch(Exception e)
		{
			_log.error("", e);
		}
		return true;
	}

	protected abstract void writeImpl();
}
