package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Decoy;
import org.l2j.gameserver.model.actor.instance.EffectPoint;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.targets.TargetType;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Summon Npc effect implementation.
 * @author Zoey76
 */
public final class SummonNpc extends AbstractEffect {
	public int despawnDelay;
	public final int npcId;
	public final int npcCount;
	private final boolean randomOffset;
	private final boolean isSummonSpawn;
	private final boolean singleInstance; // Only one instance of this NPC is allowed.
	
	public SummonNpc(StatsSet params)
	{
		despawnDelay = params.getInt("despawnDelay", 20000);
		npcId = params.getInt("npcId", 0);
		npcCount = params.getInt("npcCount", 1);
		randomOffset = params.getBoolean("randomOffset", false);
		isSummonSpawn = params.getBoolean("isSummonSpawn", false);
		singleInstance = params.getBoolean("singleInstance", false);
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.SUMMON_NPC;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (!isPlayer(effected) || effected.isAlikeDead() || effected.getActingPlayer().inObserverMode()) {
			return;
		}
		
		if (npcId <= 0 || npcCount <= 0) {
			LOGGER.warn(SummonNpc.class.getSimpleName() + ": Invalid NPC ID or count skill ID: " + skill.getId());
			return;
		}
		
		final Player player = effected.getActingPlayer();
		if (player.isMounted()) {
			return;
		}
		
		final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(npcId);
		if (isNull(npcTemplate)) {
			LOGGER.warn("Spawn of the nonexisting NPC ID: {}, skill ID: {}",  npcId, skill.getId());
			return;
		}
		
		int x = player.getX();
		int y = player.getY();
		int z = player.getZ();
		
		if (skill.getTargetType() == TargetType.GROUND) {
			final Location wordPosition = player.getActingPlayer().getCurrentSkillWorldPosition();
			if (nonNull(wordPosition)) {
				x = wordPosition.getX();
				y = wordPosition.getY();
				z = wordPosition.getZ();
			}
		} else {
			x = effected.getX();
			y = effected.getY();
			z = effected.getZ();
		}
		
		if (randomOffset) {
			x += (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20));
			y += (Rnd.nextBoolean() ? Rnd.get(20, 50) : Rnd.get(-50, -20));
		}

		switch (npcTemplate.getType()) {
			case "L2Decoy" -> {
				final Decoy decoy = new Decoy(npcTemplate, player, despawnDelay);
				decoy.setCurrentHp(decoy.getMaxHp());
				decoy.setCurrentMp(decoy.getMaxMp());
				decoy.setHeading(player.getHeading());
				decoy.setInstance(player.getInstanceWorld());
				decoy.setSummoner(player);
				decoy.spawnMe(x, y, z);
			}
			// TODO: Implement proper signet skills.
			case "L2EffectPoint" -> {
				final EffectPoint effectPoint = new EffectPoint(npcTemplate, player);
				effectPoint.setCurrentHp(effectPoint.getMaxHp());
				effectPoint.setCurrentMp(effectPoint.getMaxMp());
				effectPoint.setIsInvul(true);
				effectPoint.setSummoner(player);
				effectPoint.setTitle(player.getName());
				effectPoint.spawnMe(x, y, z);
				despawnDelay = effectPoint.getParameters().getInt("despawn_time", 0) * 1000;
				if (despawnDelay > 0) {
					effectPoint.scheduleDespawn(despawnDelay);
				}
			}
			default -> {
				Spawn spawn;
				try {
					spawn = new Spawn(npcTemplate);
				} catch (Exception e) {
					LOGGER.warn("Unable to create spawn. " + e.getMessage(), e);
					return;
				}

				spawn.setXYZ(x, y, z);
				spawn.setHeading(player.getHeading());
				spawn.stopRespawn();

				// If only single instance is allowed, delete previous NPCs.
				if (singleInstance) {
					player.getSummonedNpcs().stream().filter(npc -> npc.getId() == npcId).forEach(Npc::deleteMe);
				}

				final Npc npc = spawn.doSpawn(isSummonSpawn);
				player.addSummonedNpc(npc); // npc.setSummoner(player);
				npc.setName(npcTemplate.getName());
				npc.setTitle(npcTemplate.getName());
				if (despawnDelay > 0) {
					npc.scheduleDespawn(despawnDelay);
				}
				npc.broadcastInfo();
			}
		}
	}
}
