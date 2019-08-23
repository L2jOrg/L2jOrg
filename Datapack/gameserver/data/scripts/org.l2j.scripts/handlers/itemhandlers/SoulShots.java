package handlers.itemhandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.items.Weapon;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.items.type.ActionType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.Broadcast;

import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class SoulShots implements IItemHandler
{
    @Override
    public boolean useItem(Playable playable, Item item, boolean forceUse)
    {
        if (!isPlayer(playable))
        {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }

        final Player activeChar = playable.getActingPlayer();
        final Item weaponInst = activeChar.getActiveWeaponInstance();
        final Weapon weaponItem = activeChar.getActiveWeaponItem();
        final List<ItemSkillHolder> skills = item.getItem().getSkills(ItemSkillType.NORMAL);
        if (skills == null)
        {
            LOGGER.warn(": is missing skills!");
            return false;
        }

        final int itemId = item.getId();

        // Check if Soul shot can be used
        if ((weaponInst == null) || (weaponItem.getSoulShotCount() == 0))
        {
            if (!activeChar.getAutoSoulShot().contains(itemId))
            {
                activeChar.sendPacket(SystemMessageId.CANNOT_USE_SOULSHOTS);
            }
            return false;
        }

        final boolean gradeCheck = item.isEtcItem() && (item.getEtcItem().getDefaultAction() == ActionType.SOULSHOT) && (weaponInst.getItem().getCrystalType() == item.getItem().getCrystalType());

        if (!gradeCheck)
        {
            if (!activeChar.getAutoSoulShot().contains(itemId))
            {
                activeChar.sendPacket(SystemMessageId.THE_SOULSHOT_YOU_ARE_ATTEMPTING_TO_USE_DOES_NOT_MATCH_THE_GRADE_OF_YOUR_EQUIPPED_WEAPON);
            }
            return false;
        }

        activeChar.soulShotLock.lock();
        try
        {
            // Check if Soul shot is already active
            if (activeChar.isChargedShot(ShotType.SOULSHOTS))
            {
                return false;
            }

            // Consume Soul shots if player has enough of them
            int SSCount = weaponItem.getSoulShotCount();
            if ((weaponItem.getReducedSoulShot() > 0) && (Rnd.get(100) < weaponItem.getReducedSoulShotChance()))
            {
                SSCount = weaponItem.getReducedSoulShot();
            }

            if (!activeChar.destroyItemWithoutTrace("Consume", item.getObjectId(), SSCount, null, false))
            {
                if (!activeChar.disableAutoShot(itemId))
                {
                    activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT);
                }
                return false;
            }
            // Charge soul shot
            activeChar.chargeShot(ShotType.SOULSHOTS);
        }
        finally
        {
            activeChar.soulShotLock.unlock();
        }

        // Send message to client
        if (!activeChar.getAutoSoulShot().contains(item.getId()))
        {
            activeChar.sendPacket(SystemMessageId.YOUR_SOULSHOTS_ARE_ENABLED);
        }

        // Visual effect change if player has equipped Ruby lvl 3 or higher
        if (activeChar.getActiveRubyJewel() != null)
        {
            Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, activeChar, activeChar.getActiveRubyJewel().getEffectId(), 1, 0, 0), 600);
        }
        else
        {
            skills.forEach(holder -> Broadcast.toSelfAndKnownPlayersInRadius(activeChar, new MagicSkillUse(activeChar, activeChar, holder.getSkillId(), holder.getSkillLevel(), 0, 0), 600));
        }
        return true;
    }
}
