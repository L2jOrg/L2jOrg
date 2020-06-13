/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.actor.instance;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.ArenaParticipantsHolder;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.entity.BlockCheckerEngine;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ExCubeGameChangePoints;
import org.l2j.gameserver.network.serverpackets.ExCubeGameExtendedChangePoints;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import org.l2j.gameserver.util.GameUtils;

/**
 * @author BiggBoss
 */
public class Block extends Monster {
    private int _colorEffect;

    public Block(NpcTemplate template) {
        super(template);
    }

    /**
     * Will change the color of the block and update the appearance in the known players clients
     *
     * @param attacker
     * @param holder
     * @param team
     */
    public void changeColor(Player attacker, ArenaParticipantsHolder holder, int team) {
        // Do not update color while sending old info
        synchronized (this) {
            final BlockCheckerEngine event = holder.getEvent();
            if (_colorEffect == 0x53) {
                // Change color
                _colorEffect = 0x00;
                // BroadCast to all known players
                broadcastPacket(new NpcInfo(this));
                increaseTeamPointsAndSend(attacker, team, event);
            } else {
                // Change color
                _colorEffect = 0x53;
                // BroadCast to all known players
                broadcastPacket(new NpcInfo(this));
                increaseTeamPointsAndSend(attacker, team, event);
            }
            // 30% chance to drop the event items
            final int random = Rnd.get(100);
            // Bond
            if ((random > 69) && (random <= 84)) {
                dropItem(13787, event, attacker);
            } else if (random > 84) {
                dropItem(13788, event, attacker);
            }
        }
    }

    /**
     * Sets if the block is red or blue. Mainly used in block spawn
     *
     * @param isRed
     */
    public void setRed(boolean isRed) {
        _colorEffect = isRed ? 0x53 : 0x00;
    }

    /**
     * @return {@code true} if the block is red at this moment, {@code false} otherwise
     */
    @Override
    public int getColorEffect() {
        return _colorEffect;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        if (GameUtils.isPlayer(attacker)) {
            return (attacker.getActingPlayer() != null) && (attacker.getActingPlayer().getBlockCheckerArena() > -1);
        }
        return true;
    }

    @Override
    public boolean doDie(Creature killer) {
        return false;
    }

    @Override
    public void onAction(Player player, boolean interact) {
        if (!canTarget(player)) {
            return;
        }

        player.setLastFolkNPC(this);

        if (player.getTarget() != this) {
            player.setTarget(this);
            getAI(); // wake up ai
        } else if (interact) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }

    private void increaseTeamPointsAndSend(Player player, int team, BlockCheckerEngine eng) {
        eng.increasePlayerPoints(player, team);

        final int timeLeft = (int) ((eng.getStarterTime() - System.currentTimeMillis()) / 1000);
        final boolean isRed = eng.getHolder().getRedPlayers().contains(player);

        final ExCubeGameChangePoints changePoints = new ExCubeGameChangePoints(timeLeft, eng.getBluePoints(), eng.getRedPoints());
        final ExCubeGameExtendedChangePoints secretPoints = new ExCubeGameExtendedChangePoints(timeLeft, eng.getBluePoints(), eng.getRedPoints(), isRed, player, eng.getPlayerPoints(player, isRed));

        eng.getHolder().broadCastPacketToTeam(changePoints);
        eng.getHolder().broadCastPacketToTeam(secretPoints);
    }

    private void dropItem(int id, BlockCheckerEngine eng, Player player) {
        final Item drop = ItemEngine.getInstance().createItem("Loot", id, 1, player, this);
        final int x = getX() + Rnd.get(50);
        final int y = getY() + Rnd.get(50);
        final int z = getZ();

        drop.dropMe(this, x, y, z);

        eng.addNewDrop(drop);
    }
}
