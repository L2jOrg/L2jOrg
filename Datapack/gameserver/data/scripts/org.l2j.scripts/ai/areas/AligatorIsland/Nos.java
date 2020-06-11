package ai.areas.AligatorIsland;


import ai.AbstractNpcAI;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nos extends AbstractNpcAI {


    private static final int NOS = 20651;
    private static final int CROKIAN = 20804;
    private static final int GUARDIANGOLEM = 21656;


    private Nos()
    {

    }




    public static AbstractNpcAI provider()
    {
        return new ai.areas.AligatorIsland.Nos();
    }
}
