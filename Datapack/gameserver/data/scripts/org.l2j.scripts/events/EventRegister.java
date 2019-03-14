package events;

import events.ChefMonkeyEvent.ChefMonkeyEvent;
import events.EveTheFortuneTeller.EveTheFortuneTeller;
import events.HappyHours.HappyHours;
import events.LetterCollector.LetterCollector;
import events.MerrySquashmas.MerrySquashmas;
import events.SquashEvent.SquashEvent;
import events.ThePowerOfLove.ThePowerOfLove;
import events.TotalRecall.TotalRecall;
import events.WatermelonNinja.WatermelonNinja;

public class EventRegister {

    public static void main(String[] args) {
        ChefMonkeyEvent.init();
        EveTheFortuneTeller.init();
        HappyHours.init();
        LetterCollector.init();
        MerrySquashmas.init();
        SquashEvent.init();
        ThePowerOfLove.init();
        TotalRecall.init();
        WatermelonNinja.init();
    }
}
