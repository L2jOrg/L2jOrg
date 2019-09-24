package handlers.itemhandlers;

import org.l2j.gameserver.enums.BroochJewel;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.util.Broadcast;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
public abstract class AbstractBeastShot implements IItemHandler {

    @Override
    public boolean useItem(Playable playable, Item item, boolean forceUse) {

        if (!isPlayer(playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }

        var owner = playable.getActingPlayer();
        if (!owner.hasSummon()) {
            owner.sendPacket(SystemMessageId.SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            return false;
        }

        var pet = playable.getPet();
        if (nonNull(pet) && pet.isDead()) {
            owner.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_SERVITOR_SAD_ISN_T_IT);
            return false;
        }

        var aliveServitors = playable.getServitors().values().stream().filter(Predicate.not(Creature::isDead)).collect(Collectors.toList());
        if (isNull(pet) && aliveServitors.isEmpty()) {
            owner.sendPacket(SystemMessageId.SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_SERVITOR_SAD_ISN_T_IT);
            return false;
        }

        var skills = item.getItem().getSkills(ItemSkillType.NORMAL);
        if (isNullOrEmpty(skills)) {
            LOGGER.warn("item {} is missing skills!", item);
            return false;
        }

        short shotConsumption = 0;
        var shotType = getShotType(item);

        if (nonNull(pet)) {
            if (!pet.isChargedShot(shotType)) {
                shotConsumption += pet.getSoulShotsPerHit();
            }
        }

        for (Summon servitor : aliveServitors) {
            if (!servitor.isChargedShot(shotType)) {
                shotConsumption += servitor.getSoulShotsPerHit();
            }
        }

        if (!owner.destroyItemWithoutTrace("Consume", item.getObjectId(), shotConsumption, null, false)) {
            if (!owner.disableAutoShot(item.getId())) {
                owner.sendPacket(getNotEnoughMessage());
            }
            return false;
        }

        if (nonNull(pet)) {
            chargeShot(owner, skills, shotType, pet);
        }

        aliveServitors.forEach(s -> chargeShot(owner, skills, shotType, s));
        return true;
    }

    private void chargeShot(Player owner, List<ItemSkillHolder> skills, ShotType shotType, Summon s) {
        var jewel = getModifyingJewel(owner);
        if (!s.isChargedShot(shotType)) {
            sendUsesMessage(owner);
            s.chargeShot(shotType);
            if (nonNull(jewel)) {
                Broadcast.toSelfAndKnownPlayersInRadius(owner, new MagicSkillUse(s, s, jewel.getEffectId(), 1, 0, 0), 600);
            } else {
                skills.forEach(holder -> Broadcast.toSelfAndKnownPlayersInRadius(owner, new MagicSkillUse(s, s, holder.getSkillId(), holder.getSkillLevel(), 0, 0), 600));
            }
        }
    }

    protected abstract ShotType getShotType(Item item);

    protected abstract void sendUsesMessage(Player player);

    protected abstract BroochJewel getModifyingJewel(Player player);

    protected abstract SystemMessageId getNotEnoughMessage();

}
