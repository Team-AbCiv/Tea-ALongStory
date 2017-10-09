package cateam.teastory.block;

import javax.annotation.Nullable;

import cateam.teastory.achievement.AchievementLoader;
import cateam.teastory.block.LitTeaDryingPan.EnumType;
import cateam.teastory.creativetab.CreativeTabsLoader;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class TeaDryingPan extends Block
{
	protected static final AxisAlignedBB TEADRYINGPAN_AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
	public TeaDryingPan()
	{
		super(Material.IRON);
		this.setHardness(3.0F);
        this.setSoundType(SoundType.METAL);
        this.setCreativeTab(CreativeTabsLoader.tabteastory);
        this.setUnlocalizedName("tea_drying_pan");
	}

	@Override
	public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return TEADRYINGPAN_AABB;
    }
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if(!heldItem.isEmpty() && heldItem.getItem() == Items.FLINT_AND_STEEL)
		{
			if (!playerIn.capabilities.isCreativeMode)
			{
				heldItem.setItemDamage(heldItem.getItemDamage() + 1);
			}
			worldIn.setBlockState(pos, BlockLoader.lit_tea_drying_pan.getStateFromMeta(1));
			return true;
		} else if (worldIn.isRemote) {
			playerIn.sendMessage(new TextComponentTranslation("teastory.tea_drying_pan.message.1"));
		}
		return true;
	}
	
	@Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
		((EntityPlayer) placer).addStat(AchievementLoader.teaDryingPan);
		return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
    }
}
