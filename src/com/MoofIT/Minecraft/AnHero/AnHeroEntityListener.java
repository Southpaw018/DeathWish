package com.MoofIT.Minecraft.AnHero;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class AnHeroEntityListener implements Listener {
	private final AnHero plugin;

	public AnHeroEntityListener(AnHero instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event)
	{
		if (!(event.getEntity() instanceof Player)) return;
		Player p = (Player)event.getEntity();

		/*
		  they're an hero
		   -player dies. get messages
		     cases to handle:
		       -pve monster
		       -pve environment
		       -pvp
		       -unknown/other
		   -broadcast messages to players online by world
		   -print messages to dynmap or irc, maybe?
		   -log to file
		*/
	}
}
