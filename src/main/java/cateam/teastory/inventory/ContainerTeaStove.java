package cateam.teastory.inventory;

import java.io.IOException;

import cateam.teastory.item.ItemLoader;
import cateam.teastory.tileentity.TileEntityTeaStove;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerTeaStove extends Container
{
	public TileEntityTeaStove tileEntity;
	private IItemHandler leafItem;
	private IItemHandler fuelItem;
	private IItemHandler driedItem;
	private IItemHandler matchaItem;
    protected int dryTime = 0;
    protected int fuelTime = 0;
    protected int fuelTotalTime = 0;
    protected int hasWater = -1;
    protected int steamTime = 0;
	
	public ContainerTeaStove(EntityPlayer player, TileEntity tileEntity)
    {
        super();
        this.leafItem = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
        this.fuelItem = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
        this.matchaItem = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);
        this.driedItem = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        this.addSlotToContainer(new SlotItemHandler(this.leafItem, 0, 53, 20)
        {
        	@Override
            public boolean isItemValid(ItemStack stack)
            {
                return !stack.isEmpty() && stack.getItem() == ItemLoader.half_dried_tea && super.isItemValid(stack);
            }
        });
        this.addSlotToContainer(new SlotItemHandler(this.fuelItem, 0, 53, 56));
        this.addSlotToContainer(new SlotItemHandler(this.matchaItem, 0, 116, 38)
        {
        	@Override
            public boolean isItemValid(ItemStack stack)
            {
                return false;
            }
        });

        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 51 + i * 18 + 33));
            }
        }

        for (int i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 142));
        }
        this.tileEntity = (TileEntityTeaStove) tileEntity;
    }
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        Slot slot = inventorySlots.get(index);

        if (slot == null || !slot.getHasStack())
        {
            return ItemStack.EMPTY;
        }

        ItemStack newStack = slot.getStack(), oldStack = newStack.copy();

        boolean isMerged = false;
        
        if (index >= 0 && index < 2)
        {
            isMerged = mergeItemStack(newStack, 3, 39, true);
        }
        else if (index >= 3 && index < 30)
        {
            isMerged = mergeItemStack(newStack, 0, 1, false)
                    || TileEntityTeaStove.isItemFuel(newStack) && mergeItemStack(newStack, 1, 2, false)
                    || mergeItemStack(newStack, 30, 39, false);
        }
        else if (index >= 30 && index < 39)
        {
        	isMerged = mergeItemStack(newStack, 0, 1, false)
                    || TileEntityTeaStove.isItemFuel(newStack) && mergeItemStack(newStack, 1, 2, false)
                    || mergeItemStack(newStack, 3, 30, false);
        }

        if (!isMerged)
        {
            return ItemStack.EMPTY;
        }

        if (newStack.getCount() == 0)
        {
            slot.putStack(ItemStack.EMPTY);
        }
        else
        {
            slot.onSlotChanged();
        }

        slot.onTake(playerIn, newStack);

        return oldStack;
    }
	
    @Override
    public boolean canInteractWith(EntityPlayer playerIn)
    {
    	return playerIn.getDistanceSq(this.tileEntity.getPos()) <= 64;
    }
    
    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();

        this.dryTime = tileEntity.getDryTime();
        this.fuelTime = tileEntity.getFuelTime();
        this.fuelTotalTime = tileEntity.getFuelTotalTime();
        this.hasWater = tileEntity.hasWater();
        this.steamTime = tileEntity.getSteamTime();

        for (IContainerListener i : this.listeners)
        {
            i.sendProgressBarUpdate(this, 0, this.dryTime);
            i.sendProgressBarUpdate(this, 1, this.fuelTime);
            i.sendProgressBarUpdate(this, 2, this.fuelTotalTime);
            i.sendProgressBarUpdate(this, 3, this.hasWater);
            i.sendProgressBarUpdate(this, 4, this.steamTime);
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data)
    {
        super.updateProgressBar(id, data);

        switch (id)
        {
        case 0:
            this.dryTime = data;
            break;
        case 1:
        	this.fuelTime = data;
        case 2:
        	this.fuelTotalTime = data;
        case 3:
        	this.hasWater = data;
        case 4:
        	this.steamTime = data;
        default:
            break;
        }
    }
    
    public int getDryTime()
    {
        return this.dryTime;
    }

    public int getTotalDryTime()
    {
        return this.tileEntity.getTotalDryTime();
    }
    
    public int getFuelTime()
    {
        return this.fuelTime;
    }

    public int getTotalFuelTime()
    {
        return this.fuelTotalTime;
    }
    
    public int hasWater()
    {
    	return this.hasWater;
    }
    
    public int getSteamTime()
    {
    	return this.steamTime;
    }
    
    public int getTotalSteamTime()
    {
        return this.tileEntity.getTotalSteamTime();
    }
}
