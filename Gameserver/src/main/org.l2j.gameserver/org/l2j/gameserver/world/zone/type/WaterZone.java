package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneId;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import org.l2j.gameserver.network.serverpackets.ServerObjectInfo;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class WaterZone extends Zone {
    public WaterZone(int id) {
        super(id);
    }

    @Override
    protected void onEnter(Creature character) {
        character.setInsideZone(ZoneId.WATER, true);

        // TODO: update to only send speed status when that packet is known
        if (isPlayer(character)) {
            final Player player = character.getActingPlayer();
            if (player.checkTransformed(transform -> !transform.canSwim())) {
                character.stopTransformation(true);
            } else {
                player.broadcastUserInfo();
            }
        } else if (isNpc(character)) {
            World.getInstance().forEachVisibleObject(character, Player.class, player ->
            {
                if (character.getRunSpeed() == 0) {
                    player.sendPacket(new ServerObjectInfo((Npc) character, player));
                } else {
                    player.sendPacket(new NpcInfo((Npc) character));
                }
            });
        }
    }

    @Override
    protected void onExit(Creature character) {
        character.setInsideZone(ZoneId.WATER, false);

        // TODO: update to only send speed status when that packet is known
        if (isPlayer(character)) {
            // Mobius: Attempt to stop water task.
            if (!character.isInsideZone(ZoneId.WATER)) {
                ((Player) character).stopWaterTask();
            }
            character.getActingPlayer().broadcastUserInfo();
        } else if (isNpc(character)) {
            World.getInstance().forEachVisibleObject(character, Player.class, player ->
            {
                if (character.getRunSpeed() == 0) {
                    player.sendPacket(new ServerObjectInfo((Npc) character, player));
                } else {
                    player.sendPacket(new NpcInfo((Npc) character));
                }
            });
        }
    }

    public int getWaterZ() {
        return getZone().getHighZ();
    }
}
