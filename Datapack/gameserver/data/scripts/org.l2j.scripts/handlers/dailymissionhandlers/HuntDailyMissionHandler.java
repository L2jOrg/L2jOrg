package handlers.dailymissionhandlers;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.dailymission.DailyMissionDataHolder;
import org.l2j.gameserver.model.dailymission.DailyMissionStatus;
import org.l2j.gameserver.model.events.Containers;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.util.MathUtil;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class HuntDailyMissionHandler extends AbstractDailyMissionHandler {

    private final int requiredLevel;
    private final int maxLevel;
    private final List<Integer> monsters;
    private final int classLevel;

    public HuntDailyMissionHandler(DailyMissionDataHolder holder) {
        super(holder);
        requiredLevel = holder.getParams().getInt("minLevel", 0);
        maxLevel = holder.getParams().getInt("maxLevel", Byte.MAX_VALUE);
        classLevel = holder.getParams().getInt("classLevel", 0);
        final String monsters = holder.getParams().getString("monsters", "");
        this.monsters = Arrays.stream(monsters.split(" ")).filter(Util::isInteger).map(Integer::parseInt).collect(Collectors.toList());
    }

    @Override
    public void init() {
        Containers.Monsters().addListener(new ConsumerEventListener(this, EventType.ON_ATTACKABLE_KILL, (Consumer<OnAttackableKill>) this::onKill, this));
    }

    private void onKill(OnAttackableKill event) {
        var monster = event.getTarget();
        if (!monsters.isEmpty() && !monsters.contains(monster.getId())) {
            return;
        }

        final var player = event.getAttacker();

        if(player.getLevel() < requiredLevel || player.getLevel() > maxLevel || (player.getLevel() - monster.getLevel() > 5) || player.getClassId().level() < classLevel) {
            return;
        }

        final var party = player.getParty();
        if (isNull(party)) {
            onKillProgress(player);
        } else {
            var channel = party.getCommandChannel();
            final List<Player> members = isNull(channel) ? party.getMembers() : channel.getMembers();
            members.stream().filter(member -> MathUtil.isInsideRadius3D(member, monster,  Config.ALT_PARTY_RANGE)).forEach(this::onKillProgress);
        }
    }

    private void onKillProgress(Player player)
    {
        final var entry = getPlayerEntry(player, true);
        if (entry.getStatus() == DailyMissionStatus.NOT_AVAILABLE)
        {
            if (entry.increaseProgress() >= getRequiredCompletion())
            {
                entry.setStatus(DailyMissionStatus.AVAILABLE);
                notifyAvailablesReward(player);
            }
            storePlayerEntry(entry);
        }
    }
}
