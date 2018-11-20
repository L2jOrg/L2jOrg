package org.l2j.gameserver.model.entity.boat;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.GetOffVehiclePacket;
import org.l2j.gameserver.network.l2.s2c.GetOnVehiclePacket;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;
import org.l2j.gameserver.network.l2.s2c.MoveToLocationInVehiclePacket;
import org.l2j.gameserver.network.l2.s2c.StopMovePacket;
import org.l2j.gameserver.network.l2.s2c.StopMoveInVehiclePacket;
import org.l2j.gameserver.network.l2.s2c.ValidateLocationInVehiclePacket;
import org.l2j.gameserver.network.l2.s2c.VehicleCheckLocationPacket;
import org.l2j.gameserver.network.l2.s2c.VehicleDeparturePacket;
import org.l2j.gameserver.network.l2.s2c.VehicleInfoPacket;
import org.l2j.gameserver.network.l2.s2c.VehicleStartPacket;
import org.l2j.gameserver.templates.CreatureTemplate;
import org.l2j.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date  17:46/26.12.2010
 */
public class Vehicle extends Boat
{
	private static final long serialVersionUID = 1L;

	public Vehicle(int objectId, CreatureTemplate template)
	{
		super(objectId, template);
	}

	@Override
	public L2GameServerPacket startPacket()
	{
		return new VehicleStartPacket(this);
	}

	@Override
	public L2GameServerPacket validateLocationPacket(Player player)
	{
		return new ValidateLocationInVehiclePacket(player);
	}

	@Override
	public L2GameServerPacket checkLocationPacket()
	{
		return new VehicleCheckLocationPacket(this);
	}

	@Override
	public L2GameServerPacket infoPacket()
	{
		return new VehicleInfoPacket(this);
	}

	@Override
	public L2GameServerPacket movePacket()
	{
		return new VehicleDeparturePacket(this);
	}

	@Override
	public L2GameServerPacket inMovePacket(Player player, Location src, Location desc)
	{
		return new MoveToLocationInVehiclePacket(player, this, src, desc);
	}

	@Override
	public L2GameServerPacket stopMovePacket()
	{
		return new StopMovePacket(this);
	}

	@Override
	public L2GameServerPacket inStopMovePacket(Player player)
	{
		return new StopMoveInVehiclePacket(player);
	}

	@Override
	public L2GameServerPacket getOnPacket(Playable playable, Location location)
	{
		if(!playable.isPlayer())
			return null;

		return new GetOnVehiclePacket(playable.getPlayer(), this, location);
	}

	@Override
	public L2GameServerPacket getOffPacket(Playable playable, Location location)
	{
		if(!playable.isPlayer())
			return null;

		return new GetOffVehiclePacket(playable.getPlayer(), this, location);
	}

	@Override
	public void oustPlayers()
	{
		//
	}

	@Override
	public boolean isVehicle()
	{
		return true;
	}
}