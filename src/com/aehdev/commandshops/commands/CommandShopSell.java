package com.aehdev.commandshops.commands;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.aehdev.commandshops.CommandShops;
import com.aehdev.commandshops.Config;
import com.aehdev.commandshops.InventoryItem;
import com.aehdev.commandshops.ItemInfo;
import com.aehdev.commandshops.PlayerData;
import com.aehdev.commandshops.Search;
import com.aehdev.commandshops.Shop;
import com.aehdev.commandshops.Transaction;

// TODO: Auto-generated Javadoc
/**
 * Processes the "/shop sell" command. This means "sell" is from the player's perspective.
 */
public class CommandShopSell extends Command
{

	/**
	 * Instantiates a new command shop sell.
	 * @param plugin
	 * the plugin
	 * @param commandLabel
	 * the command label
	 * @param sender
	 * the sender
	 * @param command
	 * the command
	 */
	public CommandShopSell(CommandShops plugin, String commandLabel, CommandSender sender, String command)
	{
		super(plugin, commandLabel, sender, command);
	}

	/* (non-Javadoc)
	 * @see com.aehdev.commandshops.commands.Command#process() */
	public boolean process()
	{
		Shop shop = null;

		// Get current shop
		if(sender instanceof Player)
		{
			// Get player & data
			Player player = (Player)sender;
			PlayerData pData = plugin.getPlayerData().get(player.getName());

			// Get Current Shop
			UUID shopUuid = pData.getCurrentShop();
			if(shopUuid != null)
			{
				shop = plugin.getShopData().getShop(shopUuid);
			}
			if(shop == null)
			{
				sender.sendMessage("You are not in a shop!");
				return true;
			}

			// Check Permissions
			if(!canUseCommand(CommandTypes.SELL))
			{
				sender.sendMessage(CommandShops.CHAT_PREFIX
						+ ChatColor.DARK_AQUA
						+ "You don't have permission to use this command");
				return true;
			}

			// sell all (player only command)
			Pattern pattern = Pattern.compile("(?i)sell\\s+all$");
			Matcher matcher = pattern.matcher(command);
			if(matcher.find())
			{
				ItemStack itemStack = player.getItemInHand();
				if(itemStack == null)
				{
					sender.sendMessage("You must be holding an item, or specify an item.");
					return true;
				}
				ItemInfo item = null;
				int amount = countItemsInInventory(player.getInventory(),
						itemStack);
				if(CommandShops.getItemList().isDurable(itemStack))
				{
					item = Search.itemById(itemStack.getTypeId());
					if(calcDurabilityPercentage(itemStack) > Config.MAX_DAMAGE
							&& Config.MAX_DAMAGE != 0)
					{
						sender.sendMessage(ChatColor.DARK_AQUA + "Your "
								+ ChatColor.WHITE + item.name
								+ ChatColor.DARK_AQUA
								+ " is too damaged to sell!");
						sender.sendMessage(ChatColor.DARK_AQUA
								+ "Items must be damanged less than "
								+ ChatColor.WHITE + Config.MAX_DAMAGE
								+ "%");
						return true;
					}
				}else
				{
					item = Search.itemById(itemStack.getTypeId(),
							itemStack.getDurability());
				}
				if(item == null)
				{
					sender.sendMessage("Could not find an item.");
					return true;
				}
				return shopSell(shop, item, amount);
			}

			// sell (player only command)
			matcher.reset();
			pattern = Pattern.compile("(?i)sell$");
			matcher = pattern.matcher(command);
			if(matcher.find())
			{
				ItemStack itemStack = player.getItemInHand();
				if(itemStack == null){ return true; }
				ItemInfo item = null;
				int amount = itemStack.getAmount();
				if(CommandShops.getItemList().isDurable(itemStack))
				{
					item = Search.itemById(itemStack.getTypeId());
					if(calcDurabilityPercentage(itemStack) > Config.MAX_DAMAGE
							&& Config.MAX_DAMAGE != 0)
					{
						sender.sendMessage(ChatColor.DARK_AQUA + "Your "
								+ ChatColor.WHITE + item.name
								+ ChatColor.DARK_AQUA
								+ " is too damaged to sell!");
						sender.sendMessage(ChatColor.DARK_AQUA
								+ "Items must be damanged less than "
								+ ChatColor.WHITE + Config.MAX_DAMAGE
								+ "%");
						return true;
					}
				}else
				{
					item = Search.itemById(itemStack.getTypeId(),
							itemStack.getDurability());
				}
				if(item == null)
				{
					sender.sendMessage("Could not find an item.");
					return true;
				}
				return shopSell(shop, item, amount);
			}
		}else
		{
			sender.sendMessage("Console is not implemented yet.");
			return true;
		}

		// Command matching

		// sell int
		Pattern pattern = Pattern.compile("(?i)sell\\s+(\\d+)");
		Matcher matcher = pattern.matcher(command);
		if(matcher.find())
		{
			int id = Integer.parseInt(matcher.group(1));
			ItemInfo item = Search.itemById(id);
			if(item == null)
			{
				sender.sendMessage("Could not find an item.");
				return true;
			}
			return shopSell(shop, item, 0);
		}

		// sell int int
		matcher.reset();
		pattern = Pattern.compile("(?i)sell\\s+(\\d+)\\s+(\\d+)");
		matcher = pattern.matcher(command);
		if(matcher.find())
		{
			int id = Integer.parseInt(matcher.group(1));
			int count = Integer.parseInt(matcher.group(2));
			ItemInfo item = Search.itemById(id);
			if(item == null)
			{
				sender.sendMessage("Could not find an item.");
				return true;
			}
			return shopSell(shop, item, count);
		}

		// sell int all
		matcher.reset();
		pattern = Pattern.compile("(?i)sell\\s+(\\d+)\\s+all");
		matcher = pattern.matcher(command);
		if(matcher.find())
		{
			int id = Integer.parseInt(matcher.group(1));
			int count = Integer.parseInt(matcher.group(2));
			ItemInfo item = Search.itemById(id);
			if(item == null)
			{
				sender.sendMessage("Could not find an item.");
				return true;
			}
			return shopSell(shop, item, count);
		}

		// sell int:int
		matcher.reset();
		pattern = Pattern.compile("(?i)sell\\s+(\\d+):(\\d+)");
		matcher = pattern.matcher(command);
		if(matcher.find())
		{
			int id = Integer.parseInt(matcher.group(1));
			short type = Short.parseShort(matcher.group(2));
			ItemInfo item = Search.itemById(id, type);
			if(item == null)
			{
				sender.sendMessage("Could not find an item.");
				return true;
			}
			return shopSell(shop, item, 0);
		}

		// sell int:int int
		matcher.reset();
		pattern = Pattern.compile("(?i)sell\\s+(\\d+):(\\d+)\\s+(\\d+)");
		matcher = pattern.matcher(command);
		if(matcher.find())
		{
			int id = Integer.parseInt(matcher.group(1));
			short type = Short.parseShort(matcher.group(2));
			ItemInfo item = Search.itemById(id, type);
			int count = Integer.parseInt(matcher.group(3));
			if(item == null)
			{
				sender.sendMessage("Could not find an item.");
				return true;
			}
			return shopSell(shop, item, count);
		}

		// sell int:int all
		matcher.reset();
		pattern = Pattern.compile("(?i)sell\\s+(\\d+):(\\d+)\\s+all");
		matcher = pattern.matcher(command);
		if(matcher.find())
		{
			int id = Integer.parseInt(matcher.group(1));
			short type = Short.parseShort(matcher.group(2));
			ItemInfo item = Search.itemById(id, type);
			int count = Integer.parseInt(matcher.group(3));
			if(item == null)
			{
				sender.sendMessage("Could not find an item.");
				return true;
			}
			return shopSell(shop, item, count);
		}

		// shop sell name, ... int
		matcher.reset();
		pattern = Pattern.compile("(?i)sell\\s+(.*)\\s+(\\d+)");
		matcher = pattern.matcher(command);
		if(matcher.find())
		{
			String itemName = matcher.group(1);
			ItemInfo item = Search.itemByName(itemName);
			int count = Integer.parseInt(matcher.group(2));
			if(item == null)
			{
				sender.sendMessage("Could not find an item.");
				return true;
			}
			return shopSell(shop, item, count);
		}

		// shop sell name, ... all
		matcher.reset();
		pattern = Pattern.compile("(?i)sell\\s+(.*)\\s+all");
		matcher = pattern.matcher(command);
		if(matcher.find())
		{
			Player player = (Player)sender;
			String itemName = matcher.group(1);
			ItemInfo item = Search.itemByName(itemName);
			if(item == null)
			{
				sender.sendMessage("Could not find an item.");
				return true;
			}
			int count = countItemsInInventory(player.getInventory(),
					item.toStack());
			return shopSell(shop, item, count);
		}

		// shop sell name, ...
		matcher.reset();
		pattern = Pattern.compile("(?i)sell\\s+(.*)");
		matcher = pattern.matcher(command);
		if(matcher.find())
		{
			String itemName = matcher.group(1);
			ItemInfo item = Search.itemByName(itemName);
			if(item == null)
			{
				sender.sendMessage("Could not find an item.");
				return true;
			}
			return shopSell(shop, item, 1);
		}

		// Show sell help
		sender.sendMessage(ChatColor.WHITE + "   /" + commandLabel
				+ " sell [itemname] [number] " + ChatColor.DARK_AQUA
				+ "- Sell this item.");
		return true;
	}

	/**
	 * Shop sell.
	 * @param shop
	 * the shop
	 * @param item
	 * the item
	 * @param amount
	 * the amount
	 * @return true, if successful
	 */
	private boolean shopSell(Shop shop, ItemInfo item, int amount)
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage("/shop sell can only be used for players!");
			return false;
		}

		Player player = (Player)sender;
		InventoryItem invItem = shop.getItem(item.name);
		PlayerData pData = plugin.getPlayerData().get(player.getName());

		// check if the shop is buying that item
		if(!shop.containsItem(item) || invItem.getSellPrice() == 0)
		{
			player.sendMessage(ChatColor.DARK_AQUA + "Sorry, "
					+ ChatColor.WHITE + shop.getName() + ChatColor.DARK_AQUA
					+ " is not buying " + ChatColor.WHITE + item.name
					+ ChatColor.DARK_AQUA + " right now.");
			return false;
		}

		// check how many items the player has
		int playerInventory = countItemsInInventory(player.getInventory(),
				item.toStack());
		if(amount < 0)
		{
			amount = 0;
		}

		// check if the amount to add is okay
		if(amount > playerInventory)
		{
			player.sendMessage(ChatColor.DARK_AQUA + "You only have "
					+ ChatColor.WHITE + playerInventory + ChatColor.DARK_AQUA
					+ " in your inventory that can be added.");
			amount = playerInventory;
		}

		// check if the shop has a max stock level set
		if(invItem.getMaxStock() != 0 && !shop.isUnlimitedStock())
		{
			if(invItem.getStock() >= invItem.getMaxStock())
			{
				player.sendMessage(ChatColor.DARK_AQUA + "Sorry, "
						+ ChatColor.WHITE + shop.getName()
						+ ChatColor.DARK_AQUA + " is not buying any more "
						+ ChatColor.WHITE + item.name + ChatColor.DARK_AQUA
						+ " right now.");
				return false;
			}

			if(amount > (invItem.getMaxStock() - invItem.getStock()))
			{
				amount = invItem.getMaxStock() - invItem.getStock();
			}
		}

		// calculate cost
		double itemPrice = invItem.getSellPrice();
		double totalCost = amount * itemPrice;

		// try to pay the player for order
		if(shop.isUnlimitedMoney())
		{
			pData.payPlayer(player.getName(), totalCost);
		}else{
			if(!isShopController(shop))
			{
				log.info(String.format("[%s] From: %s, To: %s, Cost: %f",
						plugin.pdfFile.getName(), shop.getOwner(),
						player.getName(), totalCost));
				if(!pData.payPlayer(shop.getOwner(), player.getName(),
						totalCost))
				{
					// lshop owner doesn't have enough money
					// get shop owner's balance and calculate how many it can
					// buy
					double shopBalance = plugin.getPlayerData()
							.get(player.getName()).getBalance(shop.getOwner());
					// the current shop balance must be greater than the minimum
					// balance to do the transaction.
					if(shopBalance <= shop.getMinBalance()
							|| shopBalance < invItem.getSellPrice())
					{
						player.sendMessage(ChatColor.WHITE + shop.getName()
								+ ChatColor.DARK_AQUA + " is broke!");
						return true;
					}
					// Added Min Balance calculation for maximum items the shop
					// can afford
					int amtCanAfford = (int)Math.floor(shopBalance
							- shop.getMinBalance() / itemPrice);
					totalCost = amtCanAfford * itemPrice;
					amount = amtCanAfford;
					player.sendMessage(ChatColor.DARK_AQUA + shop.getName()
							+ " could only afford " + ChatColor.WHITE
							+ amtCanAfford + ChatColor.DARK_AQUA
							+ " bundles.");
					if(!pData.payPlayer(shop.getOwner(), player.getName(),
							totalCost))
					{
						player.sendMessage(ChatColor.DARK_AQUA
								+ "Unexpected money problem: could not complete sale.");
						return true;
					}
				}
			}
		}

		if(!shop.isUnlimitedStock())
		{
			shop.addStock(item.name, amount);
		}

		if(isShopController(shop))
		{
			player.sendMessage(ChatColor.DARK_AQUA + "You added "
					+ ChatColor.WHITE + amount + " " + item.name
					+ ChatColor.DARK_AQUA + " to the shop");
		}else
		{
			player.sendMessage(ChatColor.DARK_AQUA + "You sold "
					+ ChatColor.WHITE + amount + " " + item.name
					+ ChatColor.DARK_AQUA + " and gained " + ChatColor.WHITE
					+ plugin.getEconManager().format(totalCost));
		}

		// log the transaction
		int itemInv = invItem.getStock();
		int startInv = itemInv - amount;
		if(startInv < 0)
		{
			startInv = 0;
		}
		plugin.getShopData().logItems(player.getName(), shop.getName(),
				"sell-item", item.name, amount, startInv, itemInv);
		shop.addTransaction(new Transaction(Transaction.Type.Buy, player
				.getName(), item.name, amount, totalCost));

		removeItemsFromInventory(player.getInventory(), item.toStack(), amount);
		plugin.getShopData().saveShop(shop);

		return true;
	}

}
