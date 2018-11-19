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
package l2s.authserver.network.l2.s2c;


import l2s.authserver.network.l2.L2LoginClient;
import l2s.commons.net.nio.impl.SendablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class L2LoginServerPacket extends SendablePacket<L2LoginClient>
{
	private static final Logger _log = LoggerFactory.getLogger(L2LoginServerPacket.class);

	@Override
	public final boolean write()
	{
		try
		{
			writeImpl();
			return true;
		}
		catch(Exception e)
		{
			_log.error("Client: " + getClient() + " - Failed writing: " + getClass().getSimpleName() + "!", e);
		}
		return false;
	}

	protected abstract void writeImpl();
}
