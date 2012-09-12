package com.MoofIT.Minecraft.DeathWish;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
import org.bukkit.inventory.ItemStack;

public class YouLose implements Runnable {
	private DeathWish plugin;
	private EntityDamageEvent dmgEvent;
	private Player player;
	private static Random random = new Random();

	public YouLose(DeathWish instance,EntityDamageEvent dmgEvent) {
		this.plugin = instance;
		this.dmgEvent = dmgEvent;
		this.player = (Player)dmgEvent.getEntity();
	}

	public void run() {
		String message = getMessage(dmgEvent);
		message = processMessage(message);
		String formattedMessage = formatForBroadcast(message);

		Location eventLocation = player.getLocation();
		World eventWorld = eventLocation.getWorld();
		if (plugin.alwaysBroadcast) plugin.getServer().broadcastMessage(formattedMessage);
		else {
			GoodDaySir(eventWorld,formattedMessage);
			if (!plugin.quietWorlds.contains(eventWorld.getName()) && plugin.broadcastWorlds.size() > 0) {
				for (World world : plugin.getServer().getWorlds()) {
					if (plugin.broadcastWorlds.contains(world.getName())) GoodDaySir(world, formattedMessage);
				}
			}
		}
		if (plugin.printToConsole) DeathWish.log.info("[DeathWish] Player killed: " + message + ":" + prettyPrintLocation(eventLocation));

		//TODO some of these can probably move to the main class to provide a performance boost
		if (plugin.logToFile) {
			StringBuffer filePath = new StringBuffer(plugin.logFile);
			String logPath = plugin.getDataFolder().getPath() + "/DeathWish"; //TODO test
			if (plugin.dailyFile) {
				String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				filePath.insert(filePath.lastIndexOf(".") - 1, date);
				logPath += "logs";
			}
			if (!new File(logPath).exists()) new File(logPath).mkdirs();
			File fileHandle = new File(logPath, filePath.toString());
			BufferedWriter bw;
			try {
				bw = new BufferedWriter(new FileWriter(fileHandle));
				bw.append(message);
				bw.newLine();
				bw.close();
			} catch (IOException exception) {
				DeathWish.log.severe("[DeathWish] Error saving message to log file.");
				exception.printStackTrace();
			}
		}
	}

	private String getMessage(EntityDamageEvent dmg) {
		List<String> messages = null;
		String cause = null;
		try {
			switch (dmg.getCause()) {
				case ENTITY_ATTACK:
				{
					EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)dmg;
					Entity e = event.getDamager();
					if (e == null) {
						cause = "Dispenser";
					} else if (e instanceof Player) {
						cause = "PVP";
					} else if (e instanceof PigZombie) {
						cause = "PigZombie";
					} else if (e instanceof Giant) {
						cause = "Giant";
					} else if (e instanceof Zombie) {
						cause = "Zombie";
					} else if (e instanceof Skeleton) {
						cause = "Skeleton";
					} else if (e instanceof Spider) {
						cause = "Spider";
					} else if (e instanceof Creeper) {
						cause = "Creeper";
					} else if (e instanceof Ghast) {
						cause = "Ghast";
					} else if (e instanceof Slime) {
						cause = "Slime";
					} else if (e instanceof Wolf) {
						cause = "Wolf";
					} else if (e instanceof Blaze) {
						cause = "Blaze";
					} else if (e instanceof CaveSpider) {
						cause = "CaveSpider";
					} else if (e instanceof EnderDragon) {
						cause = "EnderDragon";
					} else if (e instanceof Enderman) {
						cause = ".Enderman";
					} else if (e instanceof IronGolem) {
						cause = "IronGolem";
					} else if (e instanceof MagmaCube) {
						cause = "MagmaCube";
					} else if (e instanceof Silverfish) {
						cause = "Silverfish";
					} else {
						cause = "Other";
					}
					break;
				}
				case CONTACT:
					cause = "Cactus";
					break;
				case SUFFOCATION:
					cause = "Suffocation";
					break;
				case FALL:
					cause = "Fall";
					break;
				case FIRE:
					cause = "Fire";
					break;
				case FIRE_TICK:
					cause = "Burning";
					break;
				case LAVA:
					cause = "Lava";
					break;
				case DROWNING:
					cause = "Drowning";
					break;
				case BLOCK_EXPLOSION:
					cause = "Other";
					break;
				case ENTITY_EXPLOSION:
				{
					try {
						EntityDamageByEntityEvent event = (EntityDamageByEntityEvent)dmg;
						Entity e = event.getDamager();
						if (e instanceof TNTPrimed) cause = "TNT";
						else if (e instanceof Fireball) cause = "Ghast";
						else cause = "Creeper";
					} catch (Exception e) {
						cause = "Other";
					}
					break;
				}
				case VOID:
					cause = "Void";
					break;
				case LIGHTNING:
					cause = "Lightning";
					break;
				case CUSTOM:
					messages = Arrays.asList("%d died %n more times in the last " + plugin.cooldownTime + " seconds.");
					break;
				default:
					cause = "Unknown";
					break;
			}
			messages = plugin.messages.get(cause);
			if (messages == null || messages.size() == 0) cause = "Unknown";
		} catch (NullPointerException e) {
			DeathWish.log.severe("[DeathWish] Error processing death cause: " + dmg.getCause().toString());
			cause = "Unknown";
		}
		return messages.get(random.nextInt(messages.size())).concat(plugin.displayDeathCause ? " (" + cause + ")" : null);
	}

	private String processMessage(String finalMessage) {
		//Always replace %d and %w
		finalMessage = finalMessage.replace("%d",player.getName());
		finalMessage = finalMessage.replace("%w", player.getWorld().getName());

		if (finalMessage.contains("%n")) return finalMessage.replace("%n",Integer.toString(dmgEvent.getDamage()));

		if (!finalMessage.contains("%a") && !finalMessage.contains("%i")) return finalMessage; //Skip the complicated stuff if we can

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
			finalMessage = finalMessage.replace("%a",((Player)entityKiller).getName());

			ItemStack item = ((Player)entityKiller).getItemInHand();
			int typeID = item.getTypeId();
			//TODO add damage values

			finalMessage = finalMessage.replace("%i",Integer.toString(typeID)); //TODO item lookup
		}
		else if (blockKiller != null) { //PvE death
			finalMessage = finalMessage.replace("%a",Integer.toString(blockKiller.getTypeId())); //TODO block lookup
		}
		else { //Monster death
			finalMessage = finalMessage.replace("%a",Integer.toString(entityKiller.getEntityId())); //TODO entity lookup
		}
		return finalMessage;
	}

	String formatForBroadcast(String broadcastMessage) {
		return ChatColor.GRAY + "[DeathWish] " + ChatColor.WHITE + broadcastMessage;
	}

	void GoodDaySir(World world, String message) {
		for (Player player : world.getPlayers()) {
			player.sendMessage(message);
		}		
	}

	String prettyPrintLocation(Location location) {
		String prettified = null;

		prettified += "(";
		prettified += location.getWorld();
		prettified += ":";
		prettified += location.getBlockX();
		prettified += ",";
		prettified += location.getBlockY();
		prettified += ",";
		prettified += location.getBlockZ();
		prettified += ")";

		return prettified;
	}
}
