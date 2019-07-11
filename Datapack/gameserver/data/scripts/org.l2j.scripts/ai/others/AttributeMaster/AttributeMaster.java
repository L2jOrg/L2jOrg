package ai.others.AttributeMaster;

import ai.AbstractNpcAI;
import org.l2j.gameserver.data.elemental.ElementalSpirit;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnElementalSpiritLearn;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritInfo;

import java.util.Arrays;

import static java.util.Objects.isNull;

public class AttributeMaster extends AbstractNpcAI {

    private static final int SVEIN = 34053;

    private AttributeMaster() {
        addStartNpc(SVEIN);
        addTalkId(SVEIN);
    }

    @Override
    public String onAdvEvent(String event, Npc npc, Player player) {
        if("learn".equalsIgnoreCase(event)) {
            if(!canLearn(player)) {
                return "no-3rdClass.htm";
            }

            if(isNull(player.getSpirits())) {
                player.initElementalSpirits();
            }

            if(Arrays.stream(player.getSpirits()).allMatch(elementalSpirit -> elementalSpirit.getStage() > 0)) {
                return "already.htm";
            }

            for (ElementalSpirit spirit : player.getSpirits()) {
                if(spirit.getStage() == 0) {
                    spirit.upgrade();
                }
            }
            UserInfo userInfo = new UserInfo(player);
            userInfo.addComponentType(UserInfoType.ATT_SPIRITS);
            player.sendPacket(userInfo);
            player.sendPacket(new ElementalSpiritInfo(player.getActiveElementalSpiritType(), (byte) 0x01));
            EventDispatcher.getInstance().notifyEventAsync(new OnElementalSpiritLearn(player), player);

            return "learn.htm";
        }
        return super.onAdvEvent(event, npc, player);
    }

    private boolean canLearn(Player player) {
        return player.getLevel() >= 76 && player.getClassId().level() > 2;
    }

    public static AbstractNpcAI provider() {
        return new AttributeMaster();
    }
}
