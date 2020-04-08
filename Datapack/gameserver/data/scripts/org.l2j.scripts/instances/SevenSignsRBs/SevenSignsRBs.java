/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package instances.SevenSignsRBs;

import java.util.List;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.Npc;


import instances.AbstractInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.type.NoRestartZone;

import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * @author RobikBobik
 * @NOTE: Retail like work
 * @TODO: When one RB die, the second will be invul for 5 minutes.
 */
public class SevenSignsRBs extends AbstractInstance
{
    // NPCs
    private static final int ANAKIM_GATEKEEPER_SPIRIT = 31089;
    private static final int LILITH_GATEKEEPER_SPIRIT = 31087;
    private static final int GATEKEEPER_SPIRIT_OUT_TELEPORT = 31088;
    private static final int ANAKIM = 25286;
    private static final int LILITH = 25283;

    // Misc
    private static final int ANAKIM_TEMPLATE_ID = 200;
    private static final int LILITH_TEMPLATE_ID = 199;

    private static int MAX_PLAYERS_IN_ZONE = 300;

    private static final NoRestartZone _anakim_zone = ZoneManager.getInstance().getZoneById(70052, NoRestartZone.class);
    private static final NoRestartZone _lilith_zone = ZoneManager.getInstance().getZoneById(70053, NoRestartZone.class);

    // TELEPORTS
    private static final Location[] TELEPORT_TO_DARK_ELVEN =
            {
                    new Location(12168, 17149, -4575),
                    new Location(11688, 18219, -4585),
                    new Location(10502, 17112, -4588),
                    new Location(11169, 15922, -4585),
            };

    // TODO: When teleport from instance done. Enable it
    /*
     * private static final Location[] TELEPORT_TO_ADEN = { new Location(148053, 26935, -2206), new Location(148053, 28017, -2269), new Location(146558, 28017, -2269), new Location(146558, 26935, -2206), };
     */

    public SevenSignsRBs()
    {
        super(ANAKIM_TEMPLATE_ID, LILITH_TEMPLATE_ID);
        addStartNpc(ANAKIM_GATEKEEPER_SPIRIT, LILITH_GATEKEEPER_SPIRIT);
        addTalkId(ANAKIM_GATEKEEPER_SPIRIT, LILITH_GATEKEEPER_SPIRIT, GATEKEEPER_SPIRIT_OUT_TELEPORT);
        addKillId(ANAKIM, LILITH);
        addAttackId(ANAKIM, LILITH);
        addInstanceLeaveId(ANAKIM_TEMPLATE_ID, LILITH_TEMPLATE_ID);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player)
    {
        switch (event)
        {
            case "ANAKIM_ENTER":
            {
                if (player.isInParty())
                {
                    final Party party = player.getParty();
                    final boolean isInCC = party.isInCommandChannel();
                    final List<Player> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();

                    if (members.size() > (MAX_PLAYERS_IN_ZONE - _anakim_zone.getPlayersInsideCount()))
                    {
                        player.sendMessage("Lilith Sanctum reached 300 players. You cannot enter now.");
                    }
                    else
                    {
                        for (Player member : members)
                        {
                            if (isInsideRadius3D(member, npc, 1000))
                            {
                                player.sendMessage("Player " + member.getName() + " must go closer to Gatekeeper Spirit.");
                            }
                            enterInstance(member, npc, ANAKIM_TEMPLATE_ID);
                        }
                    }
                }
                else if (player.isGM())
                {
                    enterInstance(player, npc, ANAKIM_TEMPLATE_ID);
                    player.sendMessage("SYS: You have entered as GM/Admin to Anakim Instance");
                }
                else
                {
                    if (isInsideRadius3D(player, npc, 1000))
                    {
                        player.sendMessage("You must go closer to Gatekeeper Spirit.");
                    }
                    enterInstance(player, npc, ANAKIM_TEMPLATE_ID);
                }
                break;
            }
            case "LILITH_ENTER":
            {
                if (player.isInParty())
                {
                    final Party party = player.getParty();
                    final boolean isInCC = party.isInCommandChannel();
                    final List<Player> members = (isInCC) ? party.getCommandChannel().getMembers() : party.getMembers();

                    if (members.size() > (MAX_PLAYERS_IN_ZONE - _lilith_zone.getPlayersInsideCount()))
                    {
                        player.sendMessage("Lilith Sanctum reached 300 players. You cannot enter now.");
                    }
                    else
                    {
                        for (Player member : members)
                        {
                            if (isInsideRadius3D(member, npc, 1000))
                            {
                                player.sendMessage("Player " + member.getName() + " must go closer to Gatekeeper Spirit.");
                            }
                            enterInstance(member, npc, LILITH_TEMPLATE_ID);
                        }
                    }
                }
                else if (player.isGM())
                {
                    enterInstance(player, npc, LILITH_TEMPLATE_ID);
                    player.sendMessage("SYS: You have entered as GM/Admin to Anakim Instance");
                }
                else
                {
                    if (isInsideRadius3D(player, npc, 1000))
                    {
                        player.sendMessage("You must go closer to Gatekeeper Spirit.");
                    }
                    enterInstance(player, npc, LILITH_TEMPLATE_ID);
                }
                break;
            }
       /*     case "REMOVE_PLAYERS_FROM_ZONE_ANAKIM":
            {
                for (Creature charInside : _anakim_zone.getCharactersInside())
                {

                    if (charInside != null)
                    {
                        if (charInside.isPlayer())
                        {
                            charInside.teleToLocation(-20185 + getRandom(50), 13476 + getRandom(50), -4901);
                        }
                    }
                }
                break;
            }
            case "REMOVE_PLAYERS_FROM_ZONE_LILITH":
            {
                for (Creature charInside : _lilith_zone.getCharactersInside())
                {
                    if (charInside != null)
                    {
                        if (charInside.isPlayer())
                        {
                            charInside.teleToLocation(171346 + getRandom(50), -17599 + getRandom(50), -4901);
                        }
                    }
                }
                break;
            }*/
            case "TELEPORT_OUT":
            {
                // TODO: Different teleport location from instance.
                // switch (player.getInstanceId())
                // {
                // case ANAKIM_TEMPLATE_ID:
                // {
                // final Location destination = TELEPORT_TO_DARK_ELVEN[getRandom(TELEPORT_TO_DARK_ELVEN.length)];
                // player.teleToLocation(destination.getX() + getRandom(100), destination.getY() + getRandom(100), destination.getZ());
                // break;
                // }
                // case LILITH_TEMPLATE_ID:
                // {
                // final Location destination = TELEPORT_TO_ADEN[getRandom(TELEPORT_TO_ADEN.length)];
                // player.teleToLocation(destination.getX() + getRandom(100), destination.getY() + getRandom(100), destination.getZ());
                // break;
                // }
                // }
                final Location destination = TELEPORT_TO_DARK_ELVEN[getRandom(TELEPORT_TO_DARK_ELVEN.length)];
                player.teleToLocation(destination.getX() + getRandom(100), destination.getY() + getRandom(100), destination.getZ());
                break;
            }
            case "ANAKIM_DEATH_CAST_LILITH_INVUL":
            {
                // TODO: When one RB die, the second will be invul for 5 minutes.
                break;
            }
            case "LILITH_DEATH_CAST_ANAKIM_INVUL":
            {
                // TODO: When one RB die, the second will be invul for 5 minutes.
                break;
            }
        }
        return null;
    }

    @Override
    public String onKill(Npc npc, Player player, boolean isSummon)
    {
        switch (npc.getId())
        {
            case ANAKIM:
            {
                // TODO:
                // startQuestTimer("ANAKIM_DEATH_CAST_LILITH_INVUL", 1000, null, null);
                startQuestTimer("REMOVE_PLAYERS_FROM_ZONE_ANAKIM", 600000, null, player);
                addSpawn(GATEKEEPER_SPIRIT_OUT_TELEPORT, -6664, 18501, -5495, 0, false, 600000, false, npc.getInstanceId());
                break;
            }
            case LILITH:
            {
                // TODO:
                // startQuestTimer("LILITH_DEATH_CAST_ANAKIM_INVUL", 1000, null, null);
                startQuestTimer("REMOVE_PLAYERS_FROM_ZONE_LILITH", 600000, null, player);
                addSpawn(GATEKEEPER_SPIRIT_OUT_TELEPORT, 185062, -9612, -5493, 0, false, 600000, false, npc.getInstanceId());
                break;
            }
        }
        final Instance world = npc.getInstanceWorld();
        if (world != null)
        {
            world.finishInstance();
        }
        return super.onKill(npc, player, isSummon);
    }

    public static void main(String[] args)
    {
        new SevenSignsRBs();
    }

    public static SevenSignsRBs provider()
    {
        return new SevenSignsRBs();
    }
}
