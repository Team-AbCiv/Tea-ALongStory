package starryskyline.teastory.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import starryskyline.teastory.block.TeaStove;
import starryskyline.teastory.item.ItemLoader;

public class TileEntityTeaStove extends TileEntity implements ITickable
{
	protected int dryTime = 0;
	protected int fuelTime = 0;
	protected int fuelTotalTime = 1;
	protected int hasWater = -1;
	protected int steamTime = 0;

	protected ItemStackHandler Inventory0 = new ItemStackHandler();
	protected ItemStackHandler Inventory1 = new ItemStackHandler();
	protected ItemStackHandler Inventory3 = new ItemStackHandler();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.equals(capability))
        {
            @SuppressWarnings("unchecked")
            T result = (T) (facing == EnumFacing.DOWN ? Inventory3 : facing == EnumFacing.UP ? Inventory0 : Inventory1);
            return result;
        }
        return super.getCapability(capability, facing);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.Inventory0.deserializeNBT(compound.getCompoundTag("Inventory0"));
        this.Inventory1.deserializeNBT(compound.getCompoundTag("Inventory1"));
        this.Inventory3.deserializeNBT(compound.getCompoundTag("Inventory3"));
        this.dryTime = compound.getInteger("DryTime");
        this.fuelTime = compound.getInteger("FuelTime");
        this.fuelTotalTime = compound.getInteger("FuelTotalTime");
        this.steamTime = compound.getInteger("SteamTime");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setTag("Inventory0", this.Inventory0.serializeNBT());
        compound.setTag("Inventory1", this.Inventory1.serializeNBT());
        compound.setTag("Inventory3", this.Inventory3.serializeNBT());
        compound.setInteger("DryTime", this.dryTime);
        compound.setInteger("FuelTime", this.fuelTime);
        compound.setInteger("FuelTotalTime", this.fuelTotalTime);
        compound.setInteger("SteamTime", this.steamTime);
		return super.writeToNBT(compound);
    }
    
    @Override
    public void update()
    {
    	if(!this.world.isRemote)
    	{
    		int teaLeafKind = 0;
    		ItemStack teaLeaf = Inventory0.extractItem(0, 1, true);
    		IBlockState state = this.world.getBlockState(pos);
    		if(teaLeaf.isEmpty() || (teaLeaf.getItem() != ItemLoader.half_dried_tea))
    		{
    			this.dryTime = 0;
    		}
    		else
    		{
    			if(this.steamTime > 0 && Inventory3.insertItem(0, new ItemStack(ItemLoader.matcha_leaf), true).isEmpty())
    			{
    				if(!this.isBurning())
    				{
    					this.hasFuel();
    					if(this.isBurning())
    					{
    						teaLeafKind = 2;
    					}
    				}
    				else teaLeafKind = 2;
    			}
    			else
    			{
    				if(this.hasWater() >= 1 && this.hasWater() <= 3 && Inventory3.insertItem(0, new ItemStack(ItemLoader.matcha_leaf), true).isEmpty())
    				{
    					if(this.isBurning())
    					{
    						this.steamTime = 1600;
    						this.world.setBlockState(pos.up(), Blocks.CAULDRON.getStateFromMeta(this.hasWater - 1));
    						teaLeafKind = 2;
    					}
    					else
    					{
    						this.hasFuel();
    						if(this.isBurning())
    						{
    							this.steamTime = 1600;
    							this.world.setBlockState(pos.up(), Blocks.CAULDRON.getStateFromMeta(this.hasWater - 1));
    							teaLeafKind = 2;
    						}
    					}
    				}
    				else if(Inventory3.insertItem(0, new ItemStack(ItemLoader.dried_tea), true).isEmpty() && this.hasWater() <= 0)
    				{
    					if(this.isBurning())
    					{
    						teaLeafKind = 1;
    					}
    					else if(!isBurning())
    					{
    						this.hasFuel();
    						if(this.isBurning())
        					{
        						teaLeafKind = 1;
        					}
    					}
    				}
    				else
    				{
    					this.dryTime = 0;
    				}
    			}
    		}
    		if((this.isBurning()) && (teaLeafKind != 0))
    		{
    			this.dryTime++;
    			if(this.dryTime >= 200)
    			{
    				this.dryTime = 0;
    				Inventory0.extractItem(0, 1, false);
    				switch(teaLeafKind)
    				{
    				    case 1:
    				    	Inventory3.insertItem(0, new ItemStack(ItemLoader.dried_tea), false);
    				    	break;
    				    case 2:
    				    	Inventory3.insertItem(0, new ItemStack(ItemLoader.matcha_leaf), false);
    				    	break;
    				    case 3:
    				    	break;
    				}
    			}
    		}
    		if(this.isBurning())
    		{
    			TeaStove.setState(true, this.world, this.pos);
    			--this.fuelTime;
    		}
    		else
    		{
    			TeaStove.setState(false, this.world, this.pos);
    		}
    		if(this.steamTime > 0)
    		{
    			--this.steamTime;
    		}
    	}
    }

    public boolean isBurning()
    {
        return this.fuelTime > 0;
    }
    
    public void hasFuel()
    {
    	ItemStack itemFuel = Inventory1.extractItem(0, 1, true);
	    if(isItemFuel(itemFuel))
	    {
	        this.fuelTime = getItemBurnTime(itemFuel);
	        this.fuelTotalTime = getItemBurnTime(itemFuel);
	        Inventory1.extractItem(0, 1, false);
	        this.markDirty();
	    }
	    else
	    {
    		this.dryTime = 0;
    		this.fuelTime = 0;
    		this.markDirty();
    	}
    }
    
    public static int getItemBurnTime(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return 0;
        }
        else
        {
            Item item = stack.getItem();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.AIR)
            {
                Block block = Block.getBlockFromItem(item);

                if (block == Blocks.WOODEN_SLAB)
                {
                    return 150;
                }

                if (block.getDefaultState().getMaterial() == Material.WOOD)
                {
                    return 300;
                }

                if (block == Blocks.COAL_BLOCK)
                {
                    return 16000;
                }
            }

            if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe)item).getMaterialName().equals("WOOD")) return 200;
            if (item == Items.STICK) return 100;
            if (item == Items.COAL) return 1600;
            if (item == Items.LAVA_BUCKET) return 20000;
            if (item == Item.getItemFromBlock(Blocks.SAPLING)) return 100;
            if (item == Items.BLAZE_ROD) return 2400;
            return net.minecraftforge.fml.common.registry.GameRegistry.getFuelValue(stack);
        }
    }

    public static boolean isItemFuel(ItemStack stack)
    {
        return getItemBurnTime(stack) > 0;
    }
    
    public int getDryTime()
    {
        return this.dryTime;
    }
    
    public int getTotalDryTime()
    {
        return 200;
    }
    
    public int getFuelTime()
    {
    	return this.fuelTime;
    }
    
    public int getFuelTotalTime()
    {
    	return this.fuelTotalTime;
    }
    
    public int getTotalSteamTime()
    {
    	return 1600;
    }
    
    public int hasWater()
    {
    	IBlockState state = this.world.getBlockState(pos.up());
    	if(state.getBlock() == Blocks.CAULDRON)
    	{
    		this.hasWater = Blocks.CAULDRON.getMetaFromState(state);
    	}
    	else this.hasWater = -1;
    	return this.hasWater;
    }
    
    public int getSteamTime()
    {
    	return this.steamTime;
    }
    
    public void setSteamTime(int steamTime)
    {
    	this.steamTime = steamTime;
    }
    
    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState)
    {
        return oldState.getBlock() != newState.getBlock();
    }

}
