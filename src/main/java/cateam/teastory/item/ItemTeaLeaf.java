package cateam.teastory.item;

import cateam.teastory.block.BlockLoader;
import cateam.teastory.block.Kettle;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTeaLeaf extends TSItem
{
	private Kettle kettle;
	public ItemTeaLeaf(String name, int maxstack, Kettle kettle)
	{
		super(name, maxstack);
		this.kettle = kettle;
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = playerIn.getHeldItem(hand);
		if (worldIn.isRemote)
		{
			return EnumActionResult.SUCCESS;
		}
		else if ((worldIn.getBlockState(pos).getBlock() == BlockLoader.empty_kettle) && (stack.getCount() >= 8))
		{
			int meta = BlockLoader.empty_kettle.getMetaFromState(worldIn.getBlockState(pos));
			if ((meta & 12) == 12)
			{
				worldIn.setBlockState(pos, kettle.getStateFromMeta(meta & 3));
                if (!playerIn.capabilities.isCreativeMode)
                {
            	    stack.shrink(8);
                }
                return EnumActionResult.SUCCESS;
			}
			else return EnumActionResult.PASS;
		}
		else return EnumActionResult.PASS;
	}
}
