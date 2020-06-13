/*
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
package instances.sevensigns;

import instances.AbstractInstance;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.world.zone.ZoneManager;

import java.util.Objects;

import static org.l2j.commons.util.Util.doIfNonNull;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * @author RobikBobik
 * @author JoeAlisson
 * TODO: When one RB die, the second will be invul for 5 minutes.
 */
public class SevenSigns extends AbstractInstance {

    private static final int ANAKIM_GATEKEEPER_SPIRIT = 31089;
    private static final int LILITH_GATEKEEPER_SPIRIT = 31087;
    private static final int GATEKEEPER_SPIRIT_OUT_TELEPORT = 31088;
    private static final int ANAKIM = 25286;
    private static final int LILITH = 25283;

    private static final int ANAKIM_INSTANCE_TEMPLATE_ID = 200;
    private static final int LILITH_INSTANCE_TEMPLATE_ID = 199;
    private static final int ANAKIM_ZONE_ID = 70052;
    private static final int LILITH_ZONE_ID = 70053;

    private static final int MAX_PLAYERS_IN_ZONE = 300;

    public SevenSigns() {
        super(ANAKIM_INSTANCE_TEMPLATE_ID, LILITH_INSTANCE_TEMPLATE_ID);
        addStartNpc(ANAKIM_GATEKEEPER_SPIRIT, LILITH_GATEKEEPER_SPIRIT);
        addTalkId(ANAKIM_GATEKEEPER_SPIRIT, LILITH_GATEKEEPER_SPIRIT, GATEKEEPER_SPIRIT_OUT_TELEPORT);
        addKillId(ANAKIM, LILITH);
        addInstanceLeaveId(ANAKIM_INSTANCE_TEMPLATE_ID, LILITH_INSTANCE_TEMPLATE_ID);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        switch (event) {
            case "ANAKIM_ENTER" -> onRaidEnter(npc, player, ANAKIM_ZONE_ID, ANAKIM_INSTANCE_TEMPLATE_ID);
            case "LILITH_ENTER" -> onRaidEnter(npc, player, LILITH_ZONE_ID, LILITH_INSTANCE_TEMPLATE_ID);
            case "CLEAR_ZONE_ANAKIM" -> teleportFromZone(ANAKIM_ZONE_ID);
            case "CLEAR_ZONE_LILITH" -> teleportFromZone(LILITH_ZONE_ID);
            case "TELEPORT_OUT" -> player.teleToLocation(TeleportWhereType.TOWN);
            case "ANAKIM_DEATH" -> castInvincibility(LILITH_INSTANCE_TEMPLATE_ID, LILITH);
            case "LILITH_DEATH" -> castInvincibility(ANAKIM_INSTANCE_TEMPLATE_ID, ANAKIM);
        }
        return null;
    }

    protected void teleportFromZone(int lilithZoneId) {
        final var zone = ZoneManager.getInstance().getZoneById(lilithZoneId);
        zone.forEachPlayer(p -> p.teleToLocation(TeleportWhereType.TOWN));
    }

    protected void castInvincibility(int lilithInstanceTemplateId, int lilith2) {
        var instances = InstanceManager.getInstance().getInstances(lilithInstanceTemplateId);
        instances.stream().map(instance -> instance.getNpc(lilith2)).filter(Objects::nonNull).forEach(lilith -> {
            var skill = SkillEngine.getInstance().getSkill(15394, 1);
            SkillCaster.triggerCast(lilith, lilith, skill);
        });
    }

    protected void onRaidEnter(Npc npc, Player player, int zoneId, int raidTemplateId) {
        if(isInsideRadius3D(player, npc, 1000)) {
            var zone = ZoneManager.getInstance().getZoneById(zoneId);
            if(zone.getPlayersInsideCount() < MAX_PLAYERS_IN_ZONE) {
                enterInstance(player, npc, raidTemplateId);
            } else {
                player.sendMessage(getZoneIsFullMessage(zoneId));
            }
        }
    }

    private String getZoneIsFullMessage(int zoneId) {
        return String.format("%s reached %d players. You cannot enter now", zoneId == ANAKIM_ZONE_ID ? "Anakim Sanctum" : "Lilith Sanctum", MAX_PLAYERS_IN_ZONE);
    }

    @Override
    public String onKill(Npc npc, Player player, boolean isSummon) {
        switch (npc.getId()) {
            case ANAKIM: {
                startQuestTimer("ANAKIM_DEATH", 1000, null, null);
                startQuestTimer("CLEAR_ZONE_ANAKIM", 600000, null, player);
                addSpawn(GATEKEEPER_SPIRIT_OUT_TELEPORT, -6664, 18501, -5495, 0, false, 600000, false, npc.getInstanceId());
                break;
            }
            case LILITH: {
                startQuestTimer("LILITH_DEATH", 1000, null, null);
                startQuestTimer("CLEAR_ZONE_LILITH", 600000, null, player);
                addSpawn(GATEKEEPER_SPIRIT_OUT_TELEPORT, 185062, -9612, -5493, 0, false, 600000, false, npc.getInstanceId());
                break;
            }
        }
        doIfNonNull(npc.getInstanceWorld(), Instance::finishInstance);
        return super.onKill(npc, player, isSummon);
    }

    public static SevenSigns provider() {
        return new SevenSigns();
    }
}
