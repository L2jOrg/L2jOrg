package ai.others.DimensionalMerchant;

import ai.AbstractNpcAI;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.PackageToList;
import org.l2j.gameserver.network.serverpackets.WareHouseWithdrawalList;

/**
 * Dimensional Merchant AI.
 * @author Mobius
 */
public class DimensionalMerchant extends AbstractNpcAI
{
    // NPC
    private static final int MERCHANT = 32478; // Dimensional Merchant
    // Others
    private static final int ATTENDANCE_REWARD_MULTISELL = 3247801;
    private static final String COMMAND_BYPASS = "Quest DimensionalMerchant ";

    private DimensionalMerchant()
    {
        addStartNpc(MERCHANT);
        addFirstTalkId(MERCHANT);
        addTalkId(MERCHANT);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player)
    {
        String htmltext = null;
        switch (event)
        {
            case "package_deposit":
            {
                if (player.getAccountChars().size() < 1)
                {
                    player.sendPacket(SystemMessageId.THAT_CHARACTER_DOES_NOT_EXIST);
                }
                else
                {
                    player.sendPacket(new PackageToList(player.getAccountChars()));
                }
                break;
            }
            case "package_withdraw":
            {
                var freight = player.getFreight();
                if (freight != null)
                {
                    if (freight.getSize() > 0)
                    {
                        player.setActiveWarehouse(freight);
                        for (var i : player.getActiveWarehouse().getItems())
                        {
                            if (i.isTimeLimitedItem() && (i.getRemainingTime() <= 0))
                            {
                                player.getActiveWarehouse().destroyItem("ItemInstance", i, player, null);
                            }
                        }
                        player.sendPacket(new WareHouseWithdrawalList(1, player, WareHouseWithdrawalList.FREIGHT));
                        player.sendPacket(new WareHouseWithdrawalList(2, player, WareHouseWithdrawalList.FREIGHT));
                    }
                    else
                    {
                        player.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
                    }
                }
                break;
            }
            case "attendance_rewards":
            {
                MultisellData.getInstance().separateAndSend(ATTENDANCE_REWARD_MULTISELL, player, null, false);
                break;
            }
        }
        return htmltext;
    }

    @RegisterEvent(EventType.ON_PLAYER_BYPASS)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerBypass(OnPlayerBypass event)
    {
        var player = event.getPlayer();
        if (event.getCommand().startsWith(COMMAND_BYPASS))
        {
            notifyEvent(event.getCommand().replace(COMMAND_BYPASS, ""), null, player);
        }
    }

    public static DimensionalMerchant provider() {
        return new DimensionalMerchant();
    }
}