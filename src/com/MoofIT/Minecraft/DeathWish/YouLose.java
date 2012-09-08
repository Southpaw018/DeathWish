package com.MoofIT.Minecraft.DeathWish;

import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.Giant;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Silverfish;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
import org.bukkit.entity.Spider;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class YouLose implements Runnable {
	private DeathWish plugin;
	private Player player;
	private EntityDamageEvent dmgEvent;
	private EntityDeathEvent deathEvent;
	private static Random random = new Random();

	public YouLose(DeathWish instance,Player player,EntityDamageEvent dmgEvent,EntityDeathEvent deathEvent) {
		this.plugin = instance;
		this.player = player;
		this.dmgEvent = dmgEvent;
		this.deathEvent = deathEvent;
	}

	public void run() {
		String message = getMessage(dmgEvent);
		message = processMessage(message);
		String formattedMessage = formatForBroadcast(message);

		if (plugin.alwaysBroadcast) plugin.getServer().broadcastMessage(formattedMessage);
		else {
			World eventWorld = deathEvent.getEntity().getLocation().getWorld();
			broadcastToWorld(eventWorld,formattedMessage);
			if (!plugin.quietWorlds.contains(eventWorld.getName()) && plugin.broadcastWorlds.size() > 0) {
				for (World world : plugin.getServer().getWorlds()) {
					if (plugin.broadcastWorlds.contains(world.getName())) broadcastToWorld(world, formattedMessage);
				}
			}
		}
		//TODO print to console

		//log to file
	}

	private String getMessage(EntityDamageEvent dmg) {
		List<String> messages = null;
		try {
			switch (dmg.getCause()) {
				case ENTITY_ATTACK:
				{
					EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)dmg;
					Entity e = event.getDamager();
					if (e == null) {
						messages = plugin.messages.get("Dispenser");
					} else if (e instanceof Player) {
						messages = plugin.messages.get("PVP");
					} else if (e instanceof PigZombie) {
						messages = plugin.messages.get("PigZombie");
					} else if (e instanceof Giant) {
						messages = plugin.messages.get("Giant");
					} else if (e instanceof Zombie) {
						messages = plugin.messages.get("Zombie");
					} else if (e instanceof Skeleton) {
						messages = plugin.messages.get("Skeleton");
					} else if (e instanceof Spider) {
						messages = plugin.messages.get("Spider");
					} else if (e instanceof Creeper) {
						messages = plugin.messages.get("Creeper");
					} else if (e instanceof Ghast) {
						messages = plugin.messages.get("Ghast");
					} else if (e instanceof Slime) {
						messages = plugin.messages.get("Slime");
					} else if (e instanceof Wolf) {
						messages = plugin.messages.get("Wolf");
					} else if (e instanceof Blaze) {
						messages = plugin.messages.get("Blaze");
					} else if (e instanceof CaveSpider) {
						messages = plugin.messages.get("CaveSpider");
					} else if (e instanceof EnderDragon) {
						messages = plugin.messages.get("EnderDragon");
					} else if (e instanceof Enderman) {
						messages = plugin.messages.get(".Enderman");
					} else if (e instanceof IronGolem) {
						messages = plugin.messages.get("IronGolem");
					} else if (e instanceof MagmaCube) {
						messages = plugin.messages.get("MagmaCube");
					} else if (e instanceof Silverfish) {
						messages = plugin.messages.get("Silverfish");
					} else {
						messages = plugin.messages.get("Other");
					}
				}
				case CONTACT:
					messages = plugin.messages.get("Cactus");
				case SUFFOCATION:
					messages = plugin.messages.get("Suffocation");
				case FALL:
					messages = plugin.messages.get("Fall");
				case FIRE:
					messages = plugin.messages.get("Fire");
				case FIRE_TICK:
					messages = plugin.messages.get("Burning");
				case LAVA:
					messages = plugin.messages.get("Lava");
				case DROWNING:
					messages = plugin.messages.get("Drowning");
				case BLOCK_EXPLOSION:
					messages = plugin.messages.get("Misc");
				case ENTITY_EXPLOSION:
				{
					try {
						EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)dmg;
						Entity e = event.getDamager();
						if (e instanceof TNTPrimed) messages = plugin.messages.get("TNT");
						else if (e instanceof Fireball) messages = plugin.messages.get("Ghast");
						else messages = plugin.messages.get("reeper");
					} catch (Exception e) {
						messages = plugin.messages.get("Misc");
					}
				}
				case VOID:
					messages = plugin.messages.get("Void");
				case LIGHTNING:
					messages = plugin.messages.get("Lightning");
				default:
					messages = plugin.messages.get("Other");
			}
		} catch (NullPointerException e) {
			DeathWish.log.severe("[DeathWish] Error processing death cause: " + dmg.getCause().toString());
			messages.add("%d died of unknown causes.");
		}
		return messages.get(random.nextInt(messages.size()));
	}

	private String processMessage(String finalMessage) {
		finalMessage.replace("%d",player.getName());
		Entity entityKiller = null;
		Block blockKiller = null;

		if (dmgEvent instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent damagerEvent = (EntityDamageByEntityEvent)dmgEvent;

			entityKiller = damagerEvent.getDamager();
			if (damagerEvent.getCause() == DamageCause.PROJECTILE) entityKiller = ((Projectile)entityKiller).getShooter();
		}
		else if (dmgEvent instanceof EntityDamageByBlockEvent) {
			EntityDamageByBlockEvent damagerEvent = (EntityDamageByBlockEvent)dmgEvent;

			blockKiller = damagerEvent.getDamager();
		}

		if (entityKiller != null && entityKiller instanceof Player) { //PVP death
			finalMessage.replace("%a",((Player)entityKiller).getName());

			ItemStack item = ((Player)entityKiller).getItemInHand();
			int typeID = item.getTypeId();
			//TODO add damage values

			finalMessage.replace("%i",Integer.toString(typeID)); //TODO item lookup
		}
		else if (blockKiller != null) { //PvE death
			finalMessage.replace("%a",Integer.toString(blockKiller.getTypeId())); //TODO block lookup
		}
		else { //Monster death
			finalMessage.replace("%a",Integer.toString(entityKiller.getEntityId())); //TODO entity lookup
		}
		return finalMessage;
	}

	String formatForBroadcast(String broadcastMessage) {
		return ChatColor.GRAY + "[DeathWish] " + ChatColor.WHITE + broadcastMessage;
	}

	void broadcastToWorld(World world, String message) {
		for (Player player : world.getPlayers()) {
			player.sendMessage(message);
		}		
	}
}
