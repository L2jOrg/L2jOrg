package handlers.bypasshandlers;


import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.equipmentupgrade.ExShowUpgradeSystem;

/**
 * @author Mobius
 */
public class EquipmentUpgrade implements IBypassHandler
{
    private static final int FERRIS = 30847;

    private static final String[] COMMANDS =
            {
                    "EquipmentUpgrade"
            };

    @Override
    public boolean useBypass(String command, Player player, Creature target)
    {
        if ((target == null) || !target.isNpc() || (target.getId() != FERRIS))
        {
            return false;
        }
        player.sendPacket(new ExShowUpgradeSystem());
        return true;
    }

    @Override
    public String[] getBypassList()
    {
        return COMMANDS;
    }
}
