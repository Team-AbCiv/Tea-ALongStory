package cateam.teastory.item;

import java.util.List;

import cateam.teastory.block.BlockLoader;
import cateam.teastory.creativetab.CreativeTabsLoader;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class TeaSeeds extends ItemSeeds
{
	public TeaSeeds()
	{
		super(BlockLoader.teaplant, Blocks.FARMLAND);
        this.setUnlocalizedName("tea_seeds");
        this.setMaxStackSize(64);
        this.setCreativeTab(CreativeTabsLoader.tabteastory);
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean b)
	{
        list.add(I18n.translateToLocal("teastory.tooltip.tea_seeds"));
    }
}
