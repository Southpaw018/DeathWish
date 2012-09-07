package com.MoofIT.Minecraft.DeathWish;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathWishEntityListener implements Listener {
	private final DeathWish plugin;

	public DeathWishEntityListener(DeathWish instance) {
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
		   -broadcast messages to players online by world, configurable
		   -cooldown for repeated identical deaths within configurable timeframe 
		   -log to file
		*/
	}
}
