package starryskyline.teastory.block;

import java.util.ArrayList;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import starryskyline.teastory.achievement.AchievementLoader;
import starryskyline.teastory.item.ItemLoader;

public class LitTeaDryingPan extends Block
{
	protected static final AxisAlignedBB TEADRYINGPAN_AABB = new AxisAlignedBB(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
	private final boolean isBurning;
	public LitTeaDryingPan()
	{
		super(Material.IRON);
		this.setHardness(3.0F);
		this.setTickRandomly(true);
        this.setSoundType(SoundType.METAL);
        this.setLightLevel(0.875F);
        this.setUnlocalizedName("lit_tea_drying_pan");
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumType.T1));
        this.isBurning = true;
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
	public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState blockstate, int fortune)
	{
    	ArrayList<ItemStack> drops = new ArrayList<>();
	    drops.add(new ItemStack(BlockLoader.tea_drying_pan, 1));
		return drops;
	}

	@Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return TEADRYINGPAN_AABB;
    }

    @Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
		if(!worldIn.isRemote)
		{
		    int meta = getMetaFromState(worldIn.getBlockState(pos));
		    if (((meta >= 2) && (meta <= 11)) && (worldIn.rand.nextFloat() < 0.5F))
		    {
			    if((meta != 5) && (meta != 9))
			    {
				    worldIn.setBlockState(pos, this.getStateFromMeta(meta + 1));
			    }
			    else
				{
					worldIn.setBlockState(pos, this.getStateFromMeta(12));
				}

	    	}
		}
    }
	
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
		int meta = getMetaFromState(worldIn.getBlockState(pos));
		if(meta >= 1)
		{
            double d0 = (double)pos.getX();
            double d1 = (double)pos.getY();
            double d2 = (double)pos.getZ();
            if(meta != 12)
            {
                worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.5D, d1 + 1.0D, d2 + 0.5D, 0.0D, 0.08D, 0.0D);
            }
            else
            	worldIn.spawnParticle(EnumParticleTypes.SMOKE_LARGE, d0 + 0.5D, d1 + 1.0D, d2 + 0.5D, 0.0D, 0.1D, 0.0D);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + worldIn.rand.nextDouble(), d1 + 0.2D, d2 + worldIn.rand.nextDouble(), 0.01D, 0.0D, 0.0D);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + worldIn.rand.nextDouble(), d1 + 0.2D, d2 + worldIn.rand.nextDouble(), 0.0D, 0.0D, 0.01D);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + worldIn.rand.nextDouble(), d1 + 0.2D, d2 + worldIn.rand.nextDouble(), 0.0D, 0.0D, -0.01D);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + worldIn.rand.nextDouble(), d1 + 0.2D, d2 + worldIn.rand.nextDouble(), -0.01D, 0.0D, 0.0D);
		}
    }

    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		int meta = getMetaFromState(worldIn.getBlockState(pos));
		if (meta == 1)
		{
			ItemStack heldItem = playerIn.getHeldItem(hand);
			if(!heldItem.isEmpty())
			{
				if((heldItem.getItem() == ItemLoader.tea_leaf) && (heldItem.getCount() >= 8))
				{
				    worldIn.setBlockState(pos, this.getStateFromMeta(2));
				    if (!playerIn.capabilities.isCreativeMode)
                    {
	            	    heldItem.shrink(8);
                    }
				    return true;
				}
			}
			if(worldIn.isRemote)
			{
			    playerIn.sendMessage(new TextComponentTranslation("teastory.tea_drying_pan.message.2"));
			}
			return true;
		}
		else if(meta == 2)
		{
			if(worldIn.isRemote)
			{
			    playerIn.sendMessage(new TextComponentTranslation("teastory.tea_drying_pan.message.3"));
			}
			return true;
		}
		else if((meta >= 3) && (meta <= 5))
		{
			worldIn.setBlockState(pos, this.getStateFromMeta(6));
			if(worldIn.isRemote)
			{
			    playerIn.sendMessage(new TextComponentTranslation("teastory.tea_drying_pan.message.4"));
			}
			return true;
		}
		else if(meta == 6)
		{
			if(worldIn.isRemote)
			{
			    playerIn.sendMessage(new TextComponentTranslation("teastory.tea_drying_pan.message.5"));
			}
			return true;
		}
		else if((meta >= 7) && (meta <= 9))
		{
			worldIn.setBlockState(pos, this.getStateFromMeta(10));
			if(worldIn.isRemote)
			{
			    playerIn.sendMessage(new TextComponentTranslation("teastory.tea_drying_pan.message.6"));
			}
			return true;
		}
		else if((meta == 10) || (meta == 11))
		{
			if(!worldIn.isRemote)
			{
			    playerIn.world.spawnEntity(new EntityItem(playerIn.world, playerIn.posX + 0.5D, playerIn.posY + 1.5D, playerIn.posZ + 0.5D,
            	        new ItemStack(ItemLoader.dried_tea, 8)));
			}
			worldIn.setBlockState(pos, BlockLoader.tea_drying_pan.getStateFromMeta(0));
			return true;
		}
		else if(meta == 12)
		{
			if(!worldIn.isRemote)
			{
				playerIn.addStat(AchievementLoader.burntLeaf);
			    playerIn.world.spawnEntity(new EntityItem(playerIn.world, playerIn.posX + 0.5D, playerIn.posY + 1.5D, playerIn.posZ + 0.5D,
            	        new ItemStack(ItemLoader.burnt_tea, 8)));
			}
			else playerIn.sendMessage(new TextComponentTranslation("teastory.tea_drying_pan.message.7"));
			worldIn.setBlockState(pos, BlockLoader.tea_drying_pan.getStateFromMeta(0));
			return true;
		}
		return false;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
	    list.add(new ItemStack(BlockLoader.tea_drying_pan, 1));
	}

	@Override // Replace the old getItem, in accordance to forge
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(BlockLoader.tea_drying_pan);
    }
	
	@Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, TYPE);
    }
	
	@Override
    public IBlockState getStateFromMeta(int meta)
    {
		if ((meta>12) || (meta<=0)) meta = 1;
        EnumType t = EnumType.values()[meta];
        return this.getDefaultState().withProperty(TYPE, t);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(TYPE).ordinal();
    }
	
	public static String getSpecialName(ItemStack stack)
    {
		return "." + String.valueOf(stack.getItemDamage());
    }
	
	public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", LitTeaDryingPan.EnumType.class);
	public enum EnumType implements IStringSerializable
    {
		T0("0"),
	    T1("1"),
		T2("2"),
		T3("3"),
		T4("4"),
		T5("5"),
		T6("6"),
		T7("7"),
		T8("8"),
		T9("9"),
		T10("10"),
		T11("11"),
		T12("12");

	    private String name;
	    
	    private EnumType(String name)
        {
	        this.name = name;
	    }
	    
	    @Override
	    public String getName()
        {
	        return name;
	    }
	    
	    @Override
	    public String toString()
        {
	        return getName();
	    }
	}
}
