module org.l2j.scripts {
    requires org.l2j.gameserver;
    requires org.slf4j;
    requires java.sql;
    requires org.l2j.commons;
    requires primitive;

    exports org.l2j.scripts.ai to org.l2j.gameserver;
    exports org.l2j.scripts.ai.door to org.l2j.gameserver;
    exports org.l2j.scripts.ai.events to org.l2j.gameserver;
    exports org.l2j.scripts.ai.locations.crumatower to org.l2j.gameserver;
    exports org.l2j.scripts.ai.locations.dragonvalley to org.l2j.gameserver;
    exports org.l2j.scripts.ai.locations.loa to org.l2j.gameserver;
    exports org.l2j.scripts.ai.locations.toi.heavenlyrift to org.l2j.gameserver;
    exports org.l2j.scripts.ai.quests._227_TestOfTheReformer to org.l2j.gameserver;
    exports org.l2j.scripts.ai.residences.castle to org.l2j.gameserver;
    exports org.l2j.scripts.ai.residences.instantclanhall to org.l2j.gameserver;
    exports org.l2j.scripts.ai.residences to org.l2j.gameserver;
    exports org.l2j.scripts.bosses;
    exports org.l2j.scripts.events to org.l2j.gameserver;
    exports org.l2j.scripts.handler.admincommands to org.l2j.gameserver;
    exports org.l2j.scripts.handler.bbs to org.l2j.gameserver;
    exports org.l2j.scripts.handler.bbs.custom to org.l2j.gameserver;
    exports org.l2j.scripts.handler.dailymissions to org.l2j.gameserver;
    exports org.l2j.scripts.handler.items to org.l2j.gameserver;
    exports org.l2j.scripts.handler.onshiftaction  to org.l2j.gameserver;
    exports org.l2j.scripts.handler.onshiftaction.commons to org.l2j.gameserver;
    exports org.l2j.scripts.handler.petition to org.l2j.gameserver;
    exports org.l2j.scripts.handler.usercommands to org.l2j.gameserver;
    exports org.l2j.scripts.handler.voicecommands to org.l2j.gameserver;
    exports org.l2j.scripts.manager to org.l2j.gameserver;
    exports org.l2j.scripts.npc.bypasses.global to org.l2j.gameserver;
    exports org.l2j.scripts.npc.model to org.l2j.gameserver;
    exports org.l2j.scripts.npc.model.events to org.l2j.gameserver;
    exports org.l2j.scripts.npc.model.heavenlyrift to org.l2j.gameserver;
    exports org.l2j.scripts.npc.model.residences to org.l2j.gameserver;
    exports org.l2j.scripts.npc.model.residences.castle to org.l2j.gameserver;
    exports org.l2j.scripts.npc.model.residences.clanhall to org.l2j.gameserver;
    exports org.l2j.scripts.npc.model.residences.instantclanhall to org.l2j.gameserver;
    exports org.l2j.scripts.quests to org.l2j.gameserver;
    exports org.l2j.scripts.services to org.l2j.gameserver;
    exports org.l2j.scripts to org.l2j.gameserver;


}