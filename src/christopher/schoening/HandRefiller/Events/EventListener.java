package christopher.schoening.HandRefiller.Events;


import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;

import christopher.schoening.HandRefiller.Main;
import christopher.schoening.HandRefiller.Materials.Tools;
import christopher.schoening.HandRefiller.Utils.Tuple;


public class EventListener implements Listener{

	private static final Logger Log = Bukkit.getLogger();
	
	@SuppressWarnings({ "deprecation"})
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		//this is just used for debugging purposes
		if(Main.ENV_DEBUG) {
			PlayerInventory i = event.getPlayer().getInventory();
			if(i.getItemInMainHand() == null || i.getItemInMainHand().getType().equals(Material.AIR)) return;
			i.getItemInMainHand().setDurability(i.getItemInMainHand().getType().getMaxDurability());
		}
	}
	
	@EventHandler
	public void onItemBreak​(PlayerItemBreakEvent event) {
		
		ItemStack brokenItem = event.getBrokenItem();
		PlayerInventory inventory = event.getPlayer().getInventory();
		
		//check hands to determine which to refill
		EquipmentSlot handToRefill = null;
		ItemStack itemInMainHand = inventory.getItemInMainHand();
		
		if(itemInMainHand.getType().equals(brokenItem.getType())) { //check if item in main hand is of same itemtype as the broken item
			if(((Damageable) itemInMainHand.getItemMeta()).getDamage() - brokenItem.getType().getMaxDurability() <= 1)	// check if item durability in mainhand matches refill condition
				handToRefill = EquipmentSlot.HAND;
		}
		
		if(handToRefill == null) {	// we havent found a match in mainhand so check offhand
			ItemStack itemInOffHand = event.getPlayer().getInventory().getItemInOffHand();
			
			if(itemInOffHand.getType().equals(brokenItem.getType())) {//check if item in offhand is of same itemtype as the broken item
					if(((Damageable) itemInOffHand.getItemMeta()).getDamage() - brokenItem.getType().getMaxDurability() <= 1)	// check if item durability in mainhand matches refill condition
						handToRefill = EquipmentSlot.OFF_HAND;
			}
		}
		
		//refill hand
		if(handToRefill != null) onRefillEventOccurred(event.getPlayer(), brokenItem, handToRefill);
	}
	
	@EventHandler
	public void onBlockPlaced(BlockPlaceEvent event) {
		
		// check if hand placed from is empty and cancel if not
		if(event.getHand() == EquipmentSlot.HAND && event.getPlayer().getInventory().getItemInMainHand().getAmount() > 1)
			return;
		if(event.getHand() == EquipmentSlot.OFF_HAND && event.getPlayer().getInventory().getItemInOffHand().getAmount() > 1)
			return;
		// check if hand placed from does not contain a tool
		if(event.getHand() == EquipmentSlot.HAND && Tools.MATERIAL_IS_TOOL(event.getPlayer().getInventory().getItemInMainHand().getType()).Key)
			return;
		if(event.getHand() == EquipmentSlot.OFF_HAND && Tools.MATERIAL_IS_TOOL(event.getPlayer().getInventory().getItemInOffHand().getType()).Key)
			return;
		
		// when planting the itemnames are off - fixing the names here
		ItemStack itemToRefill = new ItemStack(event.getBlockPlaced().getType());
		
		if(itemToRefill.getType() == Material.POTATOES) itemToRefill = new ItemStack(Material.POTATO);
		if(itemToRefill.getType() == Material.WHEAT) itemToRefill = new ItemStack(Material.WHEAT_SEEDS);
		if(itemToRefill.getType() == Material.CARROTS) itemToRefill = new ItemStack(Material.CARROT);
		if(itemToRefill.getType() == Material.BEETROOTS) itemToRefill = new ItemStack(Material.BEETROOT_SEEDS);
		if(itemToRefill.getType() == Material.COCOA) itemToRefill = new ItemStack(Material.COCOA_BEANS);
		/*nether wart, melonseeds,pumpkinseeds not working other items may not work too*/
		onRefillEventOccurred(event.getPlayer(), itemToRefill, event.getHand());
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent event) {
		
		// check if old stack is empty and cancel if not
		PlayerInventory inventory = event.getPlayer().getInventory();
		if(inventory.getItem(inventory.getHeldItemSlot()) != null) return;
		
		// TODO actually check if item was dropped from inventory and cancel if so
		
		onRefillEventOccurred(event.getPlayer(), event.getItemDrop().getItemStack(), EquipmentSlot.HAND);
	}
	
	/**
	 * onRefillEventOccurred() - method to encapsule event-logic for all events that cause a refill (blockplaced/itembroke/itemdropped).
	 * 
	 * @param player The player that needs a refill.
	 * @param item The item that will be refilled.
	 */
	private void onRefillEventOccurred(Player player, ItemStack item, EquipmentSlot handToRefill) {
	
		// delay refill by 1 tick
		// source https://github.com/mmonkey/AutoRefill -> PlaceBlock.java->onBlockPlace()
		//Main.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
			//public void run() {
				try {
					refillHeldItem(player, item, handToRefill);
				}catch(ItemRefillException e) {
					Log.severe("ItemReplacingException occured : " + e.getMessage());
				}catch(Exception e) {
					Log.severe("Generic Exception occured : " + e.getMessage());
				}
			//}
		//}, 1L);
	}
	
	//TODO add animal feed refill
	//TODO add eating refill

	/**
	 * refillHeldItem() - method to refill the held item if possible.
	 * 
	 * @author Christopher Schoening
	 * 
	 * @param player The player which is affected.
	 * @param item	The item which this method tries to refill.
	 * @param handToRefill The hand which needs to be refilled.
	 * @throws ItemRefillException Custom exception thrown if an exception needs to be thrown within this method.
	 */
	private void refillHeldItem(Player player, ItemStack item, EquipmentSlot handToRefill) throws ItemRefillException {

		if(Main.ENV_DEBUG)Log.info("Looking for: "+item.getType());
		
		PlayerInventory inventory = player.getInventory();
				
		int refiller = -1;

		// if material is a tool then it can be replaced with a different tool of its kind
		ArrayList<Integer> candidatesToRefillWith = new ArrayList<Integer>();
		Tuple<Boolean, ArrayList<Material>> tuple = Tools.MATERIAL_IS_TOOL(item.getType());	// check if the item that's being replaced is a tool and what group it belongs into
		
		// search for refiller according to item is a tool or not
		if(tuple.Key) {	// if item is a tool
			for(int i = 0; i < inventory.getSize(); i++) {
				ItemStack olditem =  inventory.getItem(i);
				
				// check if olditem exists, is a match or candidate for refilling and isn't the item the user just broke.
				if (olditem != null && i != inventory.getHeldItemSlot()) {
					if(olditem.getType() == item.getType()) {	// if we found a perfect match
						refiller = i;
						break;
					}else {
						if(tuple.Value.contains(olditem.getType()))	// check if the tool is the same type as the tool thats being replaced
							candidatesToRefillWith.add(i);	// add olditem to candidates
					}
		        }
			}
		}else {
			for(int i = 0; i < inventory.getSize(); i++) {
				ItemStack olditem =  inventory.getItem(i);
				
				// check if olditem exists, is a match for refilling and isn't the item the user just placed.
				if (olditem != null && i != inventory.getHeldItemSlot()) {
					if(olditem.getType() == item.getType()) {	// if we found a perfect match
						refiller = i;
						break;
					}
		        }
			}
		}

		// handle the cases where no refiller or candidates were found or just candidates were found
		if(refiller < 0) {
			if(candidatesToRefillWith.size() == 0) return;	// stop if no candidates found and no best match found
			refiller = candidatesToRefillWith.get(0);	// init candidate if no best match found but candidates found
		}
		
		// refill
		if(handToRefill == EquipmentSlot.OFF_HAND) {
			inventory.setItemInOffHand(inventory.getItem(refiller));	// set item in offhand
		}else {
			inventory.setItemInMainHand(inventory.getItem(refiller));	// set item in mainhand
		}
    	inventory.clear(refiller);	// clear old item from inventory
	}
	
	/**
	 * ItemRefillException - custom exception used for exceptions related to the process of refilling held items.
	 * 
	 * @author Christopher Schoening
	 *
	 */
	public class ItemRefillException extends Exception{
		
		private static final long serialVersionUID = 1L;
		
		// Parameterless Constructor
      	public ItemRefillException() {}

	    // Constructor that accepts a message
	    public ItemRefillException(String message)
	    {
	       super(message);
	    }
	}
	
}
