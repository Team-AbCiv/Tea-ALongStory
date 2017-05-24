package starryskyline.teastory.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import starryskyline.teastory.creativetab.CreativeTabsLoader;
import starryskyline.teastory.item.ItemCup;
import starryskyline.teastory.item.ItemLoader;
import starryskyline.teastory.item.ItemTeaLeaf;

public class EmptyKettle extends Kettle
{
	public EmptyKettle()
    {
		super("empty_kettle", Material.ROCK);
		this.setCreativeTab(CreativeTabsLoader.tabteastory);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(WATER, Boolean.FALSE).withProperty(BOILED, Boolean.FALSE));
	} 
	
	@Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, WATER, BOILED);
    }
	
	@Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing facing = EnumFacing.getHorizontal(meta & 3);
        boolean water = (meta & 4) != 0;
        boolean boiled = water && (meta >> 3) != 0;
        return this.getDefaultState().withProperty(FACING, facing).withProperty(WATER, water).withProperty(BOILED, boiled);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
    	int facing = state.getValue(FACING).getHorizontalIndex();
        int water = state.getValue(WATER) ? 4 : 0;
        int boiled = state.getValue(BOILED) ? 8 : 0;
        return facing | water | boiled;
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
    		int meta = getMetaFromState(state);
    	    if ((meta & 12) == 0)
    		{
    			if (!heldItem.isEmpty() && heldItem.getItem() == Items.WATER_BUCKET)
				{
					if (!playerIn.capabilities.isCreativeMode)
					{
						playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, new ItemStack(Items.BUCKET));
					}
					worldIn.setBlockState(pos, BlockLoader.empty_kettle.getStateFromMeta(meta | 4));
					return true;
				}
				else return false;
    		}
    		else if((meta & 12) == 12)
    		{
    			if (!heldItem.isEmpty())
    			{
    				if (heldItem.getItem() instanceof ItemCup)
    				{
    					if (!playerIn.capabilities.isCreativeMode)
                        {
    						heldItem.shrink(1);
        		    	}
    					int meta2 = heldItem.getItemDamage();
        	    		if (!playerIn.inventory.addItemStackToInventory(new ItemStack(ItemLoader.hot_water, 1, meta2)))
                        {
                            playerIn.getEntityWorld().spawnEntity(new EntityItem(playerIn.getEntityWorld(), playerIn.posX + 0.5D, playerIn.posY + 1.5D, playerIn.posZ + 0.5D,
                            		new ItemStack(ItemLoader.hot_water, 1, meta2)));
                        }
                		else if (playerIn instanceof EntityPlayerMP)
                        {
                            ((EntityPlayerMP)playerIn).sendContainerToPlayer(playerIn.inventoryContainer);
                        }
        	    		return true;
    				} else if (heldItem.getItem() instanceof ItemTeaLeaf) {
						if (!playerIn.capabilities.isCreativeMode)
						{
							heldItem.shrink(8);
						}
						IBlockState newState = null;
						if (heldItem.getItem() == ItemLoader.black_tea_leaf) {
							newState = BlockLoader.blacktea_kettle.getDefaultState();
						} else if (heldItem.getItem() == ItemLoader.burnt_tea) {
							newState = BlockLoader.burntgreentea_kettle.getDefaultState();
						} else if (heldItem.getItem() == ItemLoader.dried_tea) {
							newState = BlockLoader.greentea_kettle.getDefaultState();
						} else if (heldItem.getItem() == ItemLoader.matcha) {
							newState = BlockLoader.matcha_kettle.getDefaultState();
						}
						if (newState != null) {
							worldIn.setBlockState(pos, newState.withProperty(FACING, state.getValue(FACING)));
						}
						return true;
					}
    				else return false;
    			}
    			else return false;
    		}
    		else return false;
        }
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list)
    {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 4));
        list.add(new ItemStack(itemIn, 1, 12));
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
    	return state.getValue(WATER) ? state.getValue(BOILED) ? 12 : 4 : 0;
    }
    
    public static String getSpecialName(ItemStack stack)
    {
    	switch(stack.getItemDamage() >> 2)
    	{
    	    case 1:
    		    return ".water";
		    case 3:
		    	return ".boiled";
		    default:
		    	return "";
    	}
    }
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool WATER = PropertyBool.create("water");
	public static final PropertyBool BOILED = PropertyBool.create("boiled");
}
