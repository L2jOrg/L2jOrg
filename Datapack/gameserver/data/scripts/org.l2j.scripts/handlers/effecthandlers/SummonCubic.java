package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.CubicData;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.CubicTemplate;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.serverpackets.ExUserInfoCubic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;


/**
 * Summon Cubic effect implementation.
 * @author Zoey76
 * @author JoeAlisson
 */
public final class SummonCubic extends AbstractEffect {
    private static final Logger LOGGER = LoggerFactory.getLogger(SummonCubic.class);

    private final int cubicId;
    private final int cubicLvl;

    private SummonCubic(StatsSet params) {
        cubicId = params.getInt("id", -1);
        cubicLvl = (int) params.getDouble("power", 0);
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

        if (cubicId < 0) {
            LOGGER.warn("Invalid Cubic ID: {} in skill ID: {}", cubicId, skill.getId());
            return;
        }

        final Player player = effected.getActingPlayer();
        if (player.inObserverMode() || player.isMounted()) {
            return;
        }

        // If cubic is already present, it's replaced.
        final CubicInstance cubic = player.getCubicById(cubicId);
        if (nonNull(cubic)) {
            if (cubic.getTemplate().getLevel() > cubicLvl) {
                // What do we do in such case?
                return;
            }
            cubic.deactivate();
        } else {
            // If maximum amount is reached, random cubic is removed.
            // Players with no mastery can have only one cubic.
            final double allowedCubicCount = player.getStats().getValue(Stat.MAX_CUBIC, 1);
            final int currentCubicCount = player.getCubics().size();
            // Extra cubics are removed, one by one, randomly.
            if (currentCubicCount >= allowedCubicCount) {
                player.getCubics().values().stream().skip((int) (currentCubicCount * Rnd.nextDouble())).findAny().ifPresent(CubicInstance::deactivate);
            }
        }

        final CubicTemplate template = CubicData.getInstance().getCubicTemplate(cubicId, cubicLvl);
        if (isNull(template)) {
            LOGGER.warn("Attempting to summon cubic without existing template id: " + cubicId + " level: " + cubicLvl);
            return;
        }

        // Adding a new cubic.
        player.addCubic(new CubicInstance(player, effector.getActingPlayer(), template));
        player.sendPacket(new ExUserInfoCubic(player));
        player.broadcastCharInfo();
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new SummonCubic(data);
        }

        @Override
        public String effectName() {
            return "summon-cubic";
        }
    }
}
