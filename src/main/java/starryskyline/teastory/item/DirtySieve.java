package starryskyline.teastory.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class DirtySieve extends TSItem
{
	public DirtySieve()
    {
 	    super("dirty_sieve", 64);
 	    this.setContainerItem(ItemLoader.broken_tea);
 	    this.setMaxDamage(64);
    }
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
	{
		ItemStack stack = new ItemStack(ItemLoader.sieve, playerIn.getHeldItem(hand).getCount());
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
}
