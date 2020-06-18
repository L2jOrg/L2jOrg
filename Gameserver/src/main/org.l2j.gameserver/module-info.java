import org.l2j.gameserver.engine.item.container.listener.BowCrossListener;

module org.l2j.gameserver {
    requires transitive org.l2j.commons;

    requires java.sql;
    requires java.desktop;
    requires org.slf4j;
    requires transitive io.github.joealisson.mmocore;
    requires transitive io.github.joealisson.primitive;
    requires cache.api;
    requires jdk.unsupported;
    requires java.management;
    requires java.compiler;

    opens org.l2j.gameserver.settings to org.l2j.commons;
    opens org.l2j.gameserver.engine.geo.settings to org.l2j.commons;
    opens org.l2j.gameserver.data.database.data to org.l2j.commons;

    exports org.l2j.gameserver.model.actor;
    exports org.l2j.gameserver.model.actor.appearance;
    exports org.l2j.gameserver.model.actor.instance;
    exports org.l2j.gameserver.model.actor.status;
    exports org.l2j.gameserver.model.holders;
    exports org.l2j.gameserver.model.quest;
    exports org.l2j.gameserver.model.skills;
    exports org.l2j.gameserver.handler;
    exports org.l2j.gameserver.model;
    exports org.l2j.gameserver.model.skills.targets;
    exports org.l2j.gameserver.enums;
    exports org.l2j.gameserver.instancemanager;
    exports org.l2j.gameserver.model.entity;
    exports org.l2j.gameserver.network;
    exports org.l2j.gameserver.network.serverpackets;
    exports org.l2j.gameserver;
    exports org.l2j.gameserver.model.events;
    exports org.l2j.gameserver.model.events.annotations;
    exports org.l2j.gameserver.model.events.impl;
    exports org.l2j.gameserver.model.spawns;
    exports org.l2j.gameserver.util;
    exports org.l2j.gameserver.ai;
    exports org.l2j.gameserver.model.effects;
    exports org.l2j.gameserver.model.item.instance;
    exports org.l2j.gameserver.model.actor.stat;
    exports org.l2j.gameserver.model.stats;
    exports org.l2j.gameserver.model.events.impl.character.player;
    exports org.l2j.gameserver.data.xml.impl;
    exports org.l2j.gameserver.model.actor.templates;
    exports org.l2j.gameserver.model.base;
    exports org.l2j.gameserver.datatables;
    exports org.l2j.gameserver.model.punishment;
    exports org.l2j.gameserver.model.item.container;
    exports org.l2j.gameserver.engine.geo;
    exports org.l2j.gameserver.model.events.impl.character.npc;
    exports org.l2j.gameserver.model.olympiad;
    exports org.l2j.gameserver.model.html;
    exports org.l2j.gameserver.model.html.styles;
    exports org.l2j.gameserver.model.html.formatters;
    exports org.l2j.gameserver.model.html.pagehandlers;
    exports org.l2j.gameserver.model.residences;
    exports org.l2j.gameserver.model.item;
    exports org.l2j.gameserver.model.item.enchant.attribute;
    exports org.l2j.gameserver.cache;
    exports org.l2j.gameserver.data.sql.impl;
    exports org.l2j.gameserver.model.events.listeners;
    exports org.l2j.gameserver.engine.scripting;
    exports org.l2j.gameserver.model.instancezone;
    exports org.l2j.gameserver.world.zone;
    exports org.l2j.gameserver.world.zone.type;
    exports org.l2j.gameserver.network.serverpackets.ensoul;
    exports org.l2j.gameserver.model.item.auction;
    exports org.l2j.gameserver.model.events.impl.olympiad;
    exports org.l2j.gameserver.model.conditions;
    exports org.l2j.gameserver.model.item.type;
    exports org.l2j.gameserver.model.events.returns;
    exports org.l2j.gameserver.network.authcomm;
    exports org.l2j.gameserver.model.teleporter;
    exports org.l2j.gameserver.model.buylist;
    exports org.l2j.gameserver.taskmanager;
    exports org.l2j.gameserver.model.clanhallauction;
    exports org.l2j.gameserver.model.interfaces;
    exports org.l2j.gameserver.model.events.impl.item;
    exports org.l2j.gameserver.model.events.impl.sieges;
    exports org.l2j.gameserver.engine.scripting.annotations;
    exports org.l2j.gameserver.network.serverpackets.luckygame;
    exports org.l2j.gameserver.world.zone.form;
    exports org.l2j.gameserver.model.matching;
    exports org.l2j.gameserver.network.serverpackets.fishing;
    exports org.l2j.gameserver.model.cubic;
    exports org.l2j.gameserver.model.actor.request;
    exports org.l2j.gameserver.network.serverpackets.attributechange;
    exports org.l2j.gameserver.model.actor.tasks.player;
    exports org.l2j.gameserver.model.variables;
    exports org.l2j.gameserver.model.events.timers;
    exports org.l2j.gameserver.settings;
    exports org.l2j.gameserver.data.database.data;
    exports org.l2j.gameserver.data.database.dao;
    exports org.l2j.gameserver.world;
    exports org.l2j.gameserver.network.serverpackets.html;
    exports org.l2j.gameserver.engine.geo.settings;
    exports org.l2j.gameserver.data.xml;
    exports org.l2j.gameserver.data.database.announce.manager;
    exports org.l2j.gameserver.data.database.announce;
    exports org.l2j.gameserver.data.xml.model;
    exports org.l2j.gameserver.engine.mission;
    exports org.l2j.gameserver.model.events.impl.character;
    exports org.l2j.gameserver.engine.item;
    exports org.l2j.gameserver.api.elemental;
    exports org.l2j.gameserver.engine.skill.api;
    exports org.l2j.gameserver.network.serverpackets.olympiad;
    exports org.l2j.gameserver.network.serverpackets.classchange;
    exports org.l2j.gameserver.network.serverpackets.costume;
    exports org.l2j.gameserver.api.costume;
    exports org.l2j.gameserver.network.serverpackets.sessionzones;
    exports org.l2j.gameserver.network.serverpackets.item;
    exports org.l2j.gameserver.api.item;
    exports org.l2j.gameserver.network.serverpackets.attendance;
    exports org.l2j.gameserver.util.exp4j;

    uses org.l2j.gameserver.engine.scripting.IScriptingEngine;
    provides org.l2j.gameserver.engine.scripting.IScriptingEngine
        with org.l2j.gameserver.engine.scripting.java.JavaScriptingEngine;

    uses org.l2j.gameserver.api.item.PlayerInventoryListener;
    provides org.l2j.gameserver.api.item.PlayerInventoryListener
        with org.l2j.gameserver.engine.item.container.listener.ItemSkillsListener,
            org.l2j.gameserver.engine.item.container.listener.ArmorSetListener,
            org.l2j.gameserver.engine.item.container.listener.AgathionBraceletListener,
            org.l2j.gameserver.engine.item.container.listener.ArtifactBookListener,
                BowCrossListener,
            org.l2j.gameserver.engine.item.container.listener.BraceletListener,
            org.l2j.gameserver.engine.item.container.listener.BroochListener;

    uses org.l2j.gameserver.api.item.InventoryListener;
    uses org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
    provides org.l2j.gameserver.api.item.InventoryListener
        with org.l2j.gameserver.engine.item.container.listener.StatsListener;
}