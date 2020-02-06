package handlers.effecthandlers;

import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Summon effect implementation.
 * @author UnAfraid
 */
public final class Summon extends AbstractEffect {
	public final int npcId;
	public final float expMultiplier;
	public final ItemHolder consumeItem;
	public final int lifeTime;
	public final int consumeItemInterval;
	
	public Summon(StatsSet params) {
		if (params.isEmpty()) {
			throw new IllegalArgumentException("Summon effect without parameters!");
		}
		
		npcId = params.getInt("npcId");
		expMultiplier = params.getFloat("expMultiplier", 1);
		consumeItem = new ItemHolder(params.getInt("consumeItemId", 0), params.getInt("consumeItemCount", 1));
		consumeItemInterval = params.getInt("consumeItemInterval", 0);
		lifeTime = params.getInt("lifeTime", 0) > 0 ? params.getInt("lifeTime") * 1000 : -1; // Classic change.
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SUMMON;
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
		if (player.hasServitors()) {
			player.getServitors().values().forEach(s -> s.unSummon(player));
		}
		final NpcTemplate template = NpcData.getInstance().getTemplate(npcId);
		final Servitor summon = new Servitor(template, player);
		final int consumeItemInterval = (this.consumeItemInterval > 0 ? this.consumeItemInterval : (template.getRace() != Race.SIEGE_WEAPON ? 240 : 60)) * 1000;
		
		summon.setName(template.getName());
		summon.setTitle(effected.getName());
		summon.setReferenceSkill(skill.getId());
		summon.setExpMultiplier(expMultiplier);
		summon.setLifeTime(lifeTime <= 0 ? Integer.MAX_VALUE : lifeTime); // Classic hack. Resummon upon entering game.
		summon.setItemConsume(consumeItem);
		summon.setItemConsumeInterval(consumeItemInterval);

		var maxLevel = ExperienceData.getInstance().getMaxLevel();

		if (summon.getLevel() >= maxLevel) {
			summon.getStats().setExp(ExperienceData.getInstance().getExpForLevel(maxLevel - 1));
			LOGGER.warn("({}) NpcID: {} has a level above {}. Please rectify.", summon.getName(), summon.getId(), maxLevel);
		} else {
			summon.getStats().setExp(ExperienceData.getInstance().getExpForLevel(summon.getLevel() % ExperienceData.getInstance().getMaxPetLevel()));
		}
		
		// Summons must have their master buffs upon spawn.
		for (BuffInfo effect : player.getEffectList().getEffects()) {
			final Skill sk = effect.getSkill();
			if (!sk.isBad()) {
				sk.applyEffects(player, summon, false, effect.getTime());
			}
		}
		
		summon.setCurrentHp(summon.getMaxHp());
		summon.setCurrentMp(summon.getMaxMp());
		summon.setHeading(player.getHeading());
		
		player.addServitor(summon);
		
		summon.setShowSummonAnimation(true);
		summon.spawnMe();
		summon.setRunning();
	}
}
