package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.data.BoatHolder;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.entity.boat.Boat;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class ValidatePosition extends L2GameClientPacket
{
	private final Location _loc = new Location();

	private int _boatId;
	private Location _lastClientPosition;
	private Location _lastServerPosition;

	/**
	 * packet type id 0x48
	 * format:		cddddd
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_loc.x = buffer.getInt();
		_loc.y = buffer.getInt();
		_loc.z = buffer.getInt();
		_loc.h = buffer.getInt();
		_boatId = buffer.getInt();
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		if(activeChar.isTeleporting() || activeChar.isInObserverMode())
			return;

		_lastClientPosition = activeChar.getLastClientPosition();
		_lastServerPosition = activeChar.getLastServerPosition();

		if(_lastClientPosition == null)
			_lastClientPosition = activeChar.getLoc();
		if(_lastServerPosition == null)
			_lastServerPosition = activeChar.getLoc();

		if(activeChar.getX() == 0 && activeChar.getY() == 0 && activeChar.getZ() == 0)
		{
			correctPosition(activeChar);
			return;
		}

		if(activeChar.isInFlyingTransform())
		{
			// В летающей трансформе нельзя находиться на территории Aden
			if(_loc.x > -166168)
			{
				activeChar.setTransform(null);
				return;
			}

			// В летающей трансформе нельзя летать ниже, чем 0, и выше, чем 6000
			if(_loc.z <= 0 || _loc.z >= 6000)
			{
				activeChar.teleToLocation(activeChar.getLoc().setZ(Math.min(5950, Math.max(50, _loc.z))));
				return;
			}
		}

		double diff = activeChar.getDistance(_loc.x, _loc.y);
		int dz = Math.abs(_loc.z - activeChar.getZ());
		int h = _lastServerPosition.z - activeChar.getZ();

		if(_boatId > 0)
		{
			Boat boat = BoatHolder.getInstance().getBoat(_boatId);
			if(boat != null && activeChar.getBoat() == boat)
			{
				activeChar.setHeading(_loc.h);
				boat.validateLocationPacket(activeChar);
			}
			activeChar.setLastClientPosition(_loc.setH(activeChar.getHeading()));
			activeChar.setLastServerPosition(activeChar.getLoc());
			return;
		}

		// Если мы уже падаем, то отключаем все валидейты
		if(activeChar.isFalling())
		{
			diff = 0;
			dz = 0;
			h = 0;
		}

		if(h > activeChar.getBaseStats().getSafeFallHeight()) // Пока падаем, высоту не корректируем
		{
			activeChar.falling(h);
		}
		else if(dz >= (activeChar.isFlying() ? 1024 : 512))
		{
			if(activeChar.getIncorrectValidateCount() >= 3)
				activeChar.teleToClosestTown();
			else
			{
				activeChar.teleToLocation(activeChar.getLoc());
				activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
			}
		}
		else if(dz >= 256)
		{
			activeChar.validateLocation(0);
		}
		else if(_loc.z < -30000 || _loc.z > 30000)
		{
			if(activeChar.getIncorrectValidateCount() >= 3)
				activeChar.teleToClosestTown();
			else
			{
				correctPosition(activeChar);
				activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
			}
		}
		else if(diff > 1024)
		{
			if(activeChar.getIncorrectValidateCount() >= 3)
				activeChar.teleToClosestTown();
			else
			{
				activeChar.teleToLocation(activeChar.getLoc());
				activeChar.setIncorrectValidateCount(activeChar.getIncorrectValidateCount() + 1);
			}
		}
		else if(diff > 256)
		{
			//TODO реализовать NetPing и вычислять предельное отклонение исходя из пинга по формуле: 16 + (ping * activeChar.getMoveSpeed()) / 1000
			activeChar.validateLocation(1);
		}
		else
			activeChar.setIncorrectValidateCount(0);

		activeChar.setLastClientPosition(_loc.setH(activeChar.getHeading()));
		activeChar.setLastServerPosition(activeChar.getLoc());
	}

	private void correctPosition(Player activeChar)
	{
		if(activeChar.isGM())
		{
			activeChar.sendMessage("Server loc: " + activeChar.getLoc());
			activeChar.sendMessage("Correcting position...");
		}
		if(_lastServerPosition.x != 0 && _lastServerPosition.y != 0 && _lastServerPosition.z != 0)
		{
			if(GeoEngine.getNSWE(_lastServerPosition.x, _lastServerPosition.y, _lastServerPosition.z, activeChar.getGeoIndex()) == GeoEngine.NSWE_ALL)
				activeChar.teleToLocation(_lastServerPosition);
			else
				activeChar.teleToClosestTown();
		}
		else if(_lastClientPosition.x != 0 && _lastClientPosition.y != 0 && _lastClientPosition.z != 0)
		{
			if(GeoEngine.getNSWE(_lastClientPosition.x, _lastClientPosition.y, _lastClientPosition.z, activeChar.getGeoIndex()) == GeoEngine.NSWE_ALL)
				activeChar.teleToLocation(_lastClientPosition);
			else
				activeChar.teleToClosestTown();
		}
		else
			activeChar.teleToClosestTown();
	}
}