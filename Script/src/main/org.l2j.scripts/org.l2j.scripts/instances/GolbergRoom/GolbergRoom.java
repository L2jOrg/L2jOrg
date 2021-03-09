/*
 * Copyright © 2019-2021 L2JOrg
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

package org.l2j.scripts.instances.GolbergRoom;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.scripts.instances.AbstractInstance;

/**
 * @author RobikBobik, Mobius
 * @NOTE: Party instance retail like work.
 * @TODO: Golberg skills
 */
public class GolbergRoom extends AbstractInstance
{
    // NPCs
    private static final int SORA = 34091;
    private static final int GOLBERG = 18359;
    private static final int GOLBERG_TREASURE_CHEST = 18357;
    // Items
    private static final int GOLBERG_KEY_ROOM = 91636;
    // Misc
    private static final int TEMPLATE_ID = 207;

    public GolbergRoom()
    {
        super(TEMPLATE_ID);
        addStartNpc(SORA);
        addKillId(GOLBERG, GOLBERG_TREASURE_CHEST);
        addInstanceLeaveId(TEMPLATE_ID);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player)
    {
        switch (event)
        {
            case "ENTER":
            {
                final Party party = player.getParty();
                if (party == null)
                {
                    return "no_party.htm";
                }
                if (!hasQuestItems(player, GOLBERG_KEY_ROOM))
                {
                    return "no_item.htm";
                }
                takeItems(player, GOLBERG_KEY_ROOM, 1);
                enterInstance(player, npc, TEMPLATE_ID);
                final Instance world = player.getInstanceWorld();
                if (world != null)
                {
                    for (Player member : party.getMembers())
                    {
                        if (member == player)
                        {
                            continue;
                        }
                        member.teleToLocation(player, 10, world);
                    }
                    startQuestTimer("GOLBERG_MOVE", 5000, world.getNpc(GOLBERG), player);
                }
                break;
            }
            case "GOLBERG_MOVE":
            {
                final Instance world = player.getInstanceWorld();
                if (world != null)
                {
                    player.sendPacket(new ExShowScreenMessage("Rats have become kings while I've been dormant.", 5000));
                    startQuestTimer("NEXT_TEXT", 7000, world.getNpc(GOLBERG), player);
                }
                npc.moveToLocation(11711, -86508, -10928, 0);
                break;
            }
            case "NEXT_TEXT":
            {
                final Instance world = player.getInstanceWorld();
                if (world != null)
                {
                    player.sendPacket(new ExShowScreenMessage("Zaken or whatever is going wild all over the southern sea.", 5000));
                    startQuestTimer("NEXT_TEXT_2", 7000, world.getNpc(GOLBERG), player);
                }
                break;
            }
            case "NEXT_TEXT_2":
            {
                final Instance world = player.getInstanceWorld();
                if (world != null)
                {
                    player.sendPacket(new ExShowScreenMessage("Who dare enter my place? Zaken sent you?", 5000));
                }
                break;
            }
            case "SPAWN_TRESURE":
            {
                final Instance world = player.getInstanceWorld();
                if (world == null)
                {
                    return null;
                }

                if (world.getParameters().getInt("treasureCounter", 0) == 0)
                {
                    world.getParameters().set("treasureCounter", 0);
                }

                if (player.isGM())
                {
                    if (world.getParameters().getInt("treasureCounter", 0) <= 27)
                    {
                        addSpawn(GOLBERG_TREASURE_CHEST, 11708 + Rnd.get(-1000, 1000), -86505 + Rnd.get(-1000, 1000), -10928, 0, true, -1, true, player.getInstanceId());
                        startQuestTimer("SPAWN_TRESURE", 1000, npc, player);
                        world.getParameters().increaseInt("treasureCounter", 1);
                    }
                }
                else if (player.getParty() != null)
                {
                    switch (player.getParty().getMemberCount())
                    {
                        case 2:
                        {
                            if (world.getParameters().getInt("treasureCounter", 0) <= 1)
                            {
                                addSpawn(GOLBERG_TREASURE_CHEST, 11708 + Rnd.get(-1000, 1000), -86505 + Rnd.get(-1000, 1000), -10928, 0, true, -1, true, player.getInstanceId());
                                startQuestTimer("SPAWN_TRESURE", 1000, npc, player);
                                world.getParameters().increaseInt("treasureCounter", 1);
                            }
                            break;
                        }
                        case 3:
                        {
                            if (world.getParameters().getInt("treasureCounter", 0) <= 2)
                            {
                                addSpawn(GOLBERG_TREASURE_CHEST, 11708 + Rnd.get(-1000, 1000), -86505 + Rnd.get(-1000, 1000), -10928, 0, true, -1, true, player.getInstanceId());
                                startQuestTimer("SPAWN_TRESURE", 1000, npc, player);
                                world.getParameters().increaseInt("treasureCounter", 1);
                            }
                            break;
                        }
                        case 4:
                        {
                            if (world.getParameters().getInt("treasureCounter", 0) <= 4)
                            {
                                addSpawn(GOLBERG_TREASURE_CHEST, 11708 + Rnd.get(-1000, 1000), -86505 + Rnd.get(-1000, 1000), -10928, 0, true, -1, true, player.getInstanceId());
                                startQuestTimer("SPAWN_TRESURE", 1000, npc, player);
                                world.getParameters().increaseInt("treasureCounter", 1);
                            }
                            break;
                        }
                        case 5:
                        {
                            if (world.getParameters().getInt("treasureCounter", 0) <= 7)
                            {
                                addSpawn(GOLBERG_TREASURE_CHEST, 11708 + Rnd.get(-1000, 1000), -86505 + Rnd.get(-1000, 1000), -10928, 0, true, -1, true, player.getInstanceId());
                                startQuestTimer("SPAWN_TRESURE", 1000, npc, player);
                                world.getParameters().increaseInt("treasureCounter", 1);
                            }
                            break;
                        }
                        case 6:
                        {
                            if (world.getParameters().getInt("treasureCounter", 0) <= 10)
                            {
                                addSpawn(GOLBERG_TREASURE_CHEST, 11708 + Rnd.get(-1000, 1000), -86505 + Rnd.get(-1000, 1000), -10928, 0, true, -1, true, player.getInstanceId());
                                startQuestTimer("SPAWN_TRESURE", 1000, npc, player);
                                world.getParameters().increaseInt("treasureCounter", 1);
                            }
                            break;
                        }
                        case 7:
                        {
                            if (world.getParameters().getInt("treasureCounter", 0) <= 13)
                            {
                                addSpawn(GOLBERG_TREASURE_CHEST, 11708 + Rnd.get(-1000, 1000), -86505 + Rnd.get(-1000, 1000), -10928, 0, true, -1, true, player.getInstanceId());
                                startQuestTimer("SPAWN_TRESURE", 1000, npc, player);
                                world.getParameters().increaseInt("treasureCounter", 1);
                            }
                            break;
                        }
                        case 8:
                        {
                            if (world.getParameters().getInt("treasureCounter", 0) <= 16)
                            {
                                addSpawn(GOLBERG_TREASURE_CHEST, 11708 + Rnd.get(-1000, 1000), -86505 + Rnd.get(-1000, 1000), -10928, 0, true, -1, true, player.getInstanceId());
                                startQuestTimer("SPAWN_TRESURE", 1000, npc, player);
                                world.getParameters().increaseInt("treasureCounter", 1);
                            }
                            break;
                        }
                        case 9:
                        {
                            if (world.getParameters().getInt("treasureCounter", 0) <= 27)
                            {
                                addSpawn(GOLBERG_TREASURE_CHEST, 11708 + Rnd.get(-1000, 1000), -86505 + Rnd.get(-1000, 1000), -10928, 0, true, -1, true, player.getInstanceId());
                                startQuestTimer("SPAWN_TRESURE", 1000, npc, player);
                                world.getParameters().increaseInt("treasureCounter", 1);
                            }
                            break;
                        }
                    }
                }
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
            case GOLBERG:
            {
                startQuestTimer("SPAWN_TRESURE", 1000, npc, player);
                final Instance world = npc.getInstanceWorld();
                if (world != null)
                {
                    world.finishInstance();
                }
                break;
            }
            case GOLBERG_TREASURE_CHEST:
            {
                break;
            }
        }
        return super.onKill(npc, player, isSummon);
    }

    public static void main(String[] args)
    {
        new GolbergRoom();
    }
}
