package handlers.effecthandlers;

import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.AcquireSkillList;
import org.l2j.gameserver.network.serverpackets.ExStorageMaxCount;
import org.l2j.gameserver.network.serverpackets.ExSubjobInfo;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 */
public class ClassChange extends AbstractEffect
{
	private final int _index;
	private static final int IDENTITY_CRISIS_SKILL_ID = 1570;
	
	public ClassChange(StatsSet params)
	{
		_index = params.getInt("index", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {

		if (!isPlayer(effected)) {
			return;
		}

		final Player player = effected.getActingPlayer();
		if(player.isTransformed() || player.isSubClassLocked() || player.isAffectedBySkill(IDENTITY_CRISIS_SKILL_ID)) {
			player.sendMessage("You cannot switch your class right now!");
			return;
		}

		if (OlympiadManager.getInstance().isRegisteredInComp(player)) {
			OlympiadManager.getInstance().unRegisterNoble(player);
		}

		final Skill identifyCrisis = SkillData.getInstance().getSkill(IDENTITY_CRISIS_SKILL_ID, 1);
		if (identifyCrisis != null)
		{
			identifyCrisis.applyEffects(player, player);
		}

		final int activeClass = player.getClassId().getId();
		player.setActiveClass(_index);

		final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_SUCCESSFULLY_SWITCHED_S1_TO_S2);
		msg.addClassId(activeClass);
		msg.addClassId(player.getClassId().getId());
		player.sendPacket(msg);

		player.broadcastUserInfo();
		player.sendPacket(new ExStorageMaxCount(player));
		player.sendPacket(new AcquireSkillList(player));
		player.sendPacket(new ExSubjobInfo(player, SubclassInfoType.CLASS_CHANGED));

	}
}
