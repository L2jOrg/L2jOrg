package handlers.effecthandlers;

import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class HairAccessorySet extends AbstractEffect  {
	public HairAccessorySet(StatsSet params) {
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (!isPlayer(effector) || !isPlayer(effected) || effected.isAlikeDead()) {
			return;
		}
		
		final Player player = effected.getActingPlayer();
		player.setHairAccessoryEnabled(!player.isHairAccessoryEnabled());
		player.broadcastUserInfo(UserInfoType.APPAREANCE);
		player.sendPacket(player.isHairAccessoryEnabled() ? SystemMessageId.HAIR_ACCESSORIES_WILL_BE_DISPLAYED_FROM_NOW_ON : SystemMessageId.HAIR_ACCESSORIES_WILL_NO_LONGER_BE_DISPLAYED);
	}
}