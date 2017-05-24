package starryskyline.teastory.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import starryskyline.teastory.creativetab.CreativeTabsLoader;
import starryskyline.teastory.item.ItemCup;
import starryskyline.teastory.item.ItemLoader;
import starryskyline.teastory.item.ItemTeaDrink;

public class FullKettle extends Kettle
{
	private int drink;
	public FullKettle(String name, int drink)
    {
		super(name, Material.ROCK);
		this.setCreativeTab(CreativeTabsLoader.tabteastory);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, EnumType.C1));
		this.drink = drink;
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
		super.onBlockActivated(worldIn, pos, state, playerIn, hand, side, hitX, hitY, hitZ);
    	if (worldIn.isRemote)
        {
            return true;
        }
    	else
        {
        	ItemStack heldItem = playerIn.getHeldItem(hand);
    		if (!heldItem.isEmpty())
    		{
    			if (heldItem.getItem() instanceof ItemCup)
    			{
    				if (!playerIn.capabilities.isCreativeMode)
                    {
    					heldItem.shrink(1);
        		    }
    				int meta = heldItem.getItemDamage();
        	    	if (!playerIn.inventory.addItemStackToInventory(new ItemStack(this.getDrink(this), 1, meta)))
                    {
                        playerIn.getEntityWorld().spawnEntity(new EntityItem(playerIn.getEntityWorld(), playerIn.posX + 0.5D, playerIn.posY + 1.5D, playerIn.posZ + 0.5D,
                            	new ItemStack(this.getDrink(this), 1, meta)));
                    }
                	else if (playerIn instanceof EntityPlayerMP)
                    {
                        ((EntityPlayerMP)playerIn).sendContainerToPlayer(playerIn.inventoryContainer);
                    }
        	    	int meta2 = getMetaFromState(worldIn.getBlockState(pos));
    	    		if ((meta2 >> 2) == 3)
    	    		{
    	    			worldIn.setBlockState(pos, BlockLoader.empty_kettle.getStateFromMeta(meta2 & 3));
    	    		}
    	    		else 
    	    		{
    	    	    	worldIn.setBlockState(pos, this.getStateFromMeta(meta2 + 4));
    	    		}
        	    	return true;
    			}
    			else return false;
    		}
    		else return false;
        }
    }
	
	public ItemTeaDrink getDrink(FullKettle kettle)
	{
		switch(kettle.drink)
		{
		    case 1:
		    	return ItemLoader.green_tea;
		    case 2:
		    	return ItemLoader.matcha_drink;
		    case 3:
		    	return ItemLoader.black_tea;
		    default:
		    	return ItemLoader.burnt_green_tea;
		}
	}
	
	@Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, TYPE);
    }
	
	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.getHorizontal(meta & 3);
        EnumType cc = EnumType.values()[meta >> 2];
        return this.getDefaultState().withProperty(FACING, facing).withProperty(TYPE, cc);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
    	int facing = state.getValue(FACING).getHorizontalIndex();
        int cc = state.getValue(TYPE).ordinal() << 2;
        return facing | cc;
    }
    
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
    {
        IBlockState origin = super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
        return origin.withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }
    
    @Override
    public int damageDropped(IBlockState state)
    {
        return state.getValue(TYPE).ordinal() << 2;
    }
    
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
	{
	    list.add(new ItemStack(itemIn, 1, 0));
	}
	
    public static String getSpecialName(ItemStack stack)
    {
    	switch(stack.getItemDamage() >> 2)
    	{
    	    case 0:
    		    return ".4";
    	    case 1:
    		    return ".3";
		    case 2:
		    	return ".2";
		    default:
		    	return ".1";
    	}
    }
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyEnum<EnumType> TYPE = PropertyEnum.create("type", FullKettle.EnumType.class);
	public enum EnumType implements IStringSerializable
    {
		C1("0"),
	    C2("1"),
		C3("2"),
		C4("3");

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

