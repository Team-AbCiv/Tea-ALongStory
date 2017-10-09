package cateam.teastory.block;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import cateam.teastory.achievement.AchievementLoader;
import cateam.teastory.creativetab.CreativeTabsLoader;
import cateam.teastory.item.ItemLoader;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Barrel extends Block
{
	public Barrel()
	{
		super(Material.WOOD);     
        this.setHardness(0.5F);
        this.setSoundType(SoundType.WOOD);
        this.setTickRandomly(true);
        this.setUnlocalizedName("barrel");
        this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, EnumType.EMPTY));
        this.setCreativeTab(CreativeTabsLoader.tabteastory);
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
	    drops.add(new ItemStack(BlockLoader.barrel, 1));
	    int meta = BlockLoader.barrel.getMetaFromState(blockstate);
	    if ((meta == 1) || (meta == 2) || (meta == 3))
	    {
    		drops.add(new ItemStack(ItemLoader.half_dried_tea, 8));
    	}
	    else if (meta == 4)
        {
            drops.add(new ItemStack(ItemLoader.black_tea_leaf, 8));
        }
	    return drops;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        int meta = getMetaFromState(worldIn.getBlockState(pos));
        if ((meta == 3) || (meta == 2))
       	{
            float f = getFermentationChance(worldIn, pos);
            if (f == 0.0F)
            { 
              	return;
            }
            else if (rand.nextInt((int)(25.0F / f) + 1) == 0)
            {
                worldIn.setBlockState(pos, BlockLoader.barrel.getStateFromMeta(meta + 1));
            }
       	}
    }
	
	public static float getFermentationChance(World worldIn, BlockPos pos)
    {
        float f = 1.0F;
        Biome biome = worldIn.getBiome(pos);
        boolean isDaytime = worldIn.getWorldTime() % 24000L < 12000L;
        float humidity = biome.getRainfall();
        float temperature = biome.getFloatTemperature(pos);
        f = (float)((double)f * ((double)humidity >= 0.2D ? (double)humidity >= 0.5D ? (double)humidity >= 0.8D ? 1.8D : 1.6D : 1.0D : 0.4D));
        f = (float)((double)f * ((double)temperature >= 0.15D ? (double)temperature >= 0.5D ? (double)temperature >= 1.0D ? 1.6D : 1.8D : 1D : 0.4D));
        if (!worldIn.canSeeSky(pos)) return f;
        else return f = f * 0.5F;
    }
	
	@Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
		((EntityPlayer) placer).addStat(AchievementLoader.barrel);
		return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
	    list.add(new ItemStack(itemIn, 1, 0));
	}
	
    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] { TYPE });
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(TYPE, getState(meta));
    }
    
    public Comparable getState(int meta)
    {
    	switch(meta)
    	{
    	case 1:
    		return EnumType.FULL;
    	case 2:
            return EnumType.FULL2;
    	case 3:
        	return EnumType.FERMENTATION;
    	case 4:
            return EnumType.BLACKTEA;
        default:
            return EnumType.EMPTY;
    	}
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        EnumType type = (EnumType) state.getValue(TYPE);
        return type.getID();
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
    	ItemStack heldItem = playerIn.getHeldItem(hand);
        if (worldIn.isRemote)
        {
        	switch(getMetaFromState(worldIn.getBlockState(pos)))
        	{
        	    case 1:
        	    	if (!heldItem.isEmpty() && !(heldItem.getItem() instanceof ItemSpade))
        	    	{
        	    		playerIn.sendMessage(new TextComponentTranslation("teastory.barrel.message.2"));
        	    	}
        	    	return true;
        	    case 2:
        	    	if (!(playerIn.isSneaking()))
        	    	{
        	    		playerIn.sendMessage(new TextComponentTranslation("teastory.barrel.message.3"));
        	    	}
        	    	return true;
        	    case 3:
        	    	if (!(playerIn.isSneaking()))
        	    	{
        	    		playerIn.sendMessage(new TextComponentTranslation("teastory.barrel.message.4"));
        	    	}
        	    	return true;
        	    case 4:
        	    	if (!(playerIn.isSneaking()))
        	    	{
        	    		playerIn.sendMessage(new TextComponentTranslation("teastory.barrel.message.5"));
        	    	}
        	    	return true;
        	    default:
        	    	if (!heldItem.isEmpty() && !(heldItem.getItem() == ItemLoader.half_dried_tea && heldItem.getCount() >= 8) && (Block.getBlockFromItem(heldItem.getItem()) != BlockLoader.barrel))
        	    	{
        	    		playerIn.sendMessage(new TextComponentTranslation("teastory.barrel.message.1"));
        	    	}
        	    	else if((heldItem != null) && (heldItem.getItem() == ItemLoader.half_dried_tea && heldItem.stackSize >=8))
        	    	{
        	    		playerIn.addChatMessage(new TextComponentTranslation("teastory.barrel.message.2"));
        	    	}
        	    	return true;
        	}
        }
        else
        {
            switch(getMetaFromState(worldIn.getBlockState(pos)))
        	{
        	    case 1:
        	    	if (!heldItem.isEmpty())
                    {
        	    		if (playerIn.isSneaking())
        	    		{
        	    			worldIn.setBlockState(pos, BlockLoader.barrel.getStateFromMeta(0));
        	                playerIn.world.spawnEntity(new EntityItem(playerIn.world, playerIn.posX + 0.5D, playerIn.posY + 1.5D, playerIn.posZ + 0.5D,
        	                        new ItemStack(ItemLoader.half_dried_tea, 8)));
        	    			return true;
        	    		}
        	    		else
                    	{
            	       		worldIn.setBlockState(pos, BlockLoader.barrel.getStateFromMeta(2));
            	       		return true;
                    	}
                    }
        	       	else return false;
        	    case 2:
        	    	if (!heldItem.isEmpty())
                    {
        	    		if (playerIn.isSneaking())
        	    		{
        	    			worldIn.setBlockState(pos, BlockLoader.barrel.getStateFromMeta(0));
        	                playerIn.world.spawnEntity(new EntityItem(playerIn.world, playerIn.posX + 0.5D, playerIn.posY + 1.5D, playerIn.posZ + 0.5D,
        	                   		new ItemStack(ItemLoader.half_dried_tea, 8)));
        	    			return true;
        	    		}
        	    		else return false;
                    }
        	    	
        	    case 3:
        	    	if (!heldItem.isEmpty())
                    {
        	    		if (playerIn.isSneaking())
        	    		{
    	        			worldIn.setBlockState(pos, BlockLoader.barrel.getStateFromMeta(0));
        	                playerIn.world.spawnEntity(new EntityItem(playerIn.world, playerIn.posX + 0.5D, playerIn.posY + 1.5D, playerIn.posZ + 0.5D,
        	                   		new ItemStack(ItemLoader.half_dried_tea, 8)));
    	    		    	return true;
    	    	    	}
    	    		    else return false;
                    }
        	    	else return false;
        	    case 4:
        	    	if (!heldItem.isEmpty())
                    {
        	    		if (playerIn.isSneaking())
        	    		{
    	    			    worldIn.setBlockState(pos, BlockLoader.barrel.getStateFromMeta(0));
    	                    playerIn.world.spawnEntity(new EntityItem(playerIn.world, playerIn.posX + 0.5D, playerIn.posY + 1.5D, playerIn.posZ + 0.5D,
    	                    	    new ItemStack(ItemLoader.black_tea_leaf, 8)));
    	    			    return true;
    	    		    }
        	    	    else return false;
                    }
    	    		else return false;
                default:
                	if (!heldItem.isEmpty())
                    {
                		if (heldItem.getItem() == ItemLoader.half_dried_tea && heldItem.getCount() >= 8)
                		{
        	                worldIn.setBlockState(pos, BlockLoader.barrel.getStateFromMeta(1));
        	                if (!playerIn.capabilities.isCreativeMode)
                            {
        	            	    heldItem.shrink(8);
                            }
                            return true;
                		}
                		else return false;
            	    }
                	else return false;
        	}
        }
    }
	
	public static final PropertyEnum TYPE = PropertyEnum.create("type", Barrel.EnumType.class);
	
	public enum EnumType implements IStringSerializable
    {
		EMPTY(0, "empty"),
	    FULL(1, "full"),
		FULL2(2, "full2"),
		FERMENTATION(3, "fermentation"),
		BLACKTEA(4, "blacktea");

	    private int ID;
	    private String name;
	    
	    private EnumType(int ID, String name)
        {
	        this.ID = ID;
	        this.name = name;
	    }
	    
	    @Override
	    public String getName()
        {
	        return name;
	    }

	    public int getID()
        {
	        return ID;
	    }
	    
	    @Override
	    public String toString()
        {
	        return getName();
	    }
	}
}