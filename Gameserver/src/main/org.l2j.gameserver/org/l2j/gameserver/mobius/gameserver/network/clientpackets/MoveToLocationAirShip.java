package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.mobius.gameserver.instancemanager.AirShipManager;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.Location;
import org.l2j.gameserver.mobius.gameserver.model.VehiclePathPoint;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2AirShipInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;

public class MoveToLocationAirShip extends IClientIncomingPacket {
    public static final int MIN_Z = -895;
    public static final int MAX_Z = 6105;
    public static final int STEP = 300;

    private int _command;
    private int _param1;
    private int _param2 = 0;

    @Override
    public void readImpl(ByteBuffer packet) {
        _command = packet.getInt();
        _param1 = packet.getInt();
        if (packet.remaining() > 0) {
            _param2 = packet.getInt();
        }
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!activeChar.isInAirShip()) {
            return;
        }

        final L2AirShipInstance ship = activeChar.getAirShip();
        if (!ship.isCaptain(activeChar)) {
            return;
        }

        int z = ship.getZ();

        switch (_command) {
            case 0: {
                if (!ship.canBeControlled()) {
                    return;
                }
                if (_param1 < L2World.GRACIA_MAX_X) {
                    ship.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(_param1, _param2, z));
                }
                break;
            }
            case 1: {
                if (!ship.canBeControlled()) {
                    return;
                }
                ship.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                break;
            }
            case 2: {
                if (!ship.canBeControlled()) {
                    return;
                }
                if (z < L2World.GRACIA_MAX_Z) {
                    z = Math.min(z + STEP, L2World.GRACIA_MAX_Z);
                    ship.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(ship.getX(), ship.getY(), z));
                }
                break;
            }
            case 3: {
                if (!ship.canBeControlled()) {
                    return;
                }
                if (z > L2World.GRACIA_MIN_Z) {
                    z = Math.max(z - STEP, L2World.GRACIA_MIN_Z);
                    ship.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, new Location(ship.getX(), ship.getY(), z));
                }
                break;
            }
            case 4: {
                if (!ship.isInDock() || ship.isMoving()) {
                    return;
                }

                final VehiclePathPoint[] dst = AirShipManager.getInstance().getTeleportDestination(ship.getDockId(), _param1);
                if (dst == null) {
                    return;
                }

                // Consume fuel, if needed
                final int fuelConsumption = AirShipManager.getInstance().getFuelConsumption(ship.getDockId(), _param1);
                if (fuelConsumption > 0) {
                    if (fuelConsumption > ship.getFuel()) {
                        activeChar.sendPacket(SystemMessageId.YOUR_AIRSHIP_CANNOT_TELEPORT_BECAUSE_DUE_TO_LOW_FUEL);
                        return;
                    }
                    ship.setFuel(ship.getFuel() - fuelConsumption);
                }

                ship.executePath(dst);
                break;
            }
        }
    }
}
