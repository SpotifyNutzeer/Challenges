package net.codingarea.challenges.plugin.challenges.implementation.setting;

import net.codingarea.challenges.plugin.Challenges;
import net.codingarea.challenges.plugin.challenges.type.Setting;
import net.codingarea.challenges.plugin.language.Message;
import net.codingarea.challenges.plugin.management.menu.MenuType;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder.PotionBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nonnull;
import java.io.PrintStream;

/**
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 1.0
 */
public class SplitHealthSetting extends Setting {

	public SplitHealthSetting() {
		super(MenuType.SETTINGS);
	}

	@Override
	protected void onEnable() {
		setHealth();
	}

	@Nonnull
	@Override
	public ItemBuilder createDisplayItem() {
		return new PotionBuilder(Material.TIPPED_ARROW, Message.forName("item-split-health-setting")).setColor(Color.RED);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityRegainHealth(@Nonnull EntityRegainHealthEvent event) {
		if (!shouldExecuteEffect()) return;
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (ignorePlayer(player)) return;

		Bukkit.getScheduler().runTaskLater(plugin, () -> setHealth(player), 1);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (!shouldExecuteEffect()) return;
		if (!(event.getEntity() instanceof Player)) return;
		Player player = (Player) event.getEntity();
		if (ignorePlayer(player)) return;

		Bukkit.getScheduler().runTaskLater(Challenges.getInstance(), () -> setHealth(player), 1);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onJoin(@Nonnull PlayerJoinEvent event) {
		if (ignorePlayer(event.getPlayer())) return;
		setHealth();
	}

	/**
	 * Calls {@link SplitHealthSetting#setHealth(Player)} for the player with the lowest health
	 */
	private void setHealth() {
		Player player = null;

		for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
			if (player == null || player.getHealth() > currentPlayer.getHealth()) {
				player = currentPlayer;
			}

		}
		if (player == null) return;
		setHealth(player);
 	}

	private void setHealth(@Nonnull Player player) {
		if (!shouldExecuteEffect()) return;

		for (Player currentPlayer : Bukkit.getOnlinePlayers()) {
			if (ignorePlayer(currentPlayer)) continue;
			if (currentPlayer.equals(player)) continue;

			double health = player.getHealth();
			AttributeInstance attribute = currentPlayer.getAttribute(Attribute.GENERIC_MAX_HEALTH);
			if (attribute == null) return;
			if (health > attribute.getValue()) {
				health = attribute.getValue();
			}

			currentPlayer.setHealth(health);
		}

	}

}