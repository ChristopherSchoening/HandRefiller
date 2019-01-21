package christopher.schoening.HandRefiller.Materials;


import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import christopher.schoening.HandRefiller.Utils.Tuple;

public class Tools {

	// this list is not included when checking for a candidate to refill with
	public static final ArrayList<Material> SHEARS = new ArrayList<>(Arrays.asList(
			Material.SHEARS
			));
	
	public static final ArrayList<Material> FLINT_AND_STEEL = new ArrayList<>(Arrays.asList(
			Material.FLINT_AND_STEEL
			));
	
	public static final ArrayList<Material> SWORDS = new ArrayList<>(Arrays.asList(
			Material.WOODEN_SWORD,
			Material.STONE_SWORD,
			Material.IRON_SWORD,
			Material.GOLDEN_SWORD,
			Material.DIAMOND_SWORD
			));
	
	public static final ArrayList<Material> PICKAXES = new ArrayList<>(Arrays.asList(
			Material.WOODEN_PICKAXE,
			Material.STONE_PICKAXE,
			Material.IRON_PICKAXE,
			Material.GOLDEN_PICKAXE,
			Material.DIAMOND_PICKAXE
			));
	
	public static final ArrayList<Material> AXES = new ArrayList<>(Arrays.asList(
			Material.WOODEN_AXE,
			Material.STONE_AXE,
			Material.IRON_AXE,
			Material.GOLDEN_AXE,
			Material.DIAMOND_AXE
			));
	
	public static final ArrayList<Material> SHOVELS = new ArrayList<>(Arrays.asList(
			Material.WOODEN_SHOVEL,
			Material.STONE_SHOVEL,
			Material.IRON_SHOVEL,
			Material.GOLDEN_SHOVEL,
			Material.DIAMOND_SHOVEL
			));
	
	public static final ArrayList<Material> HOES = new ArrayList<>(Arrays.asList(
			Material.WOODEN_HOE,
			Material.STONE_HOE,
			Material.IRON_HOE,
			Material.GOLDEN_HOE,
			Material.DIAMOND_HOE
			));

	public static final ArrayList<ArrayList<Material>> TOOLS = new ArrayList<>(Arrays.asList(AXES, SHOVELS, PICKAXES, SWORDS, HOES, SHEARS, FLINT_AND_STEEL));

	/**
	 * MATERIAL_IS_TOOL() - method to determine whether a material is a tool or not (and which tooltype).
	 * 
	 * @param item
	 * @return Tuple of boolean which is true if the item is a tool and a Materiallist which indicates the type of tool.
	 */
	public static Tuple<Boolean, ArrayList<Material>> MATERIAL_IS_TOOL(Material material) {

		boolean itemIsTool = false;
		ArrayList<Material> toolset = null;
		
		for(ArrayList<Material> tools : TOOLS) {
			for(Material m : tools) {
				if(m == material) {
					itemIsTool = true;
					break;
				}
			}
			if(itemIsTool) {
				toolset = tools;
				break;
			}
		}
		
		return new Tuple<Boolean, ArrayList<Material>>(itemIsTool, toolset);
	}

	/**
	 * IS_SAME_TOOL_TYPE() - helper method to determine whether items are of same tool type or not.
	 * 
	 * @param items - the items to inspect.
	 * @return true if all items are of same tool type.
	 */
	public static boolean IS_SAME_TOOL_TYPE(ItemStack... items) {
		
		// find out which tooltype the first item has
		Tuple<Boolean, ArrayList<Material>> tuple = MATERIAL_IS_TOOL(items[0].getType());
		
		if(!tuple.Key) return false; // item not a tool

		// check if rest of items have same tool type (if one does not then condition not met (return false))
		for(int i = 1; i < items.length; i++) {
			if(tuple.Value != MATERIAL_IS_TOOL(items[i].getType()).Value)
				return false;
		}

		return true;
	}
	
}
