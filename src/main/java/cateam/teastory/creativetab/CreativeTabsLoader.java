package cateam.teastory.creativetab;

import cateam.teastory.item.ItemLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CreativeTabsLoader
{
    public static CreativeTabs tabteastory;

    public CreativeTabsLoader(FMLPreInitializationEvent event)
    {
        tabteastory = new CreativeTabs("tabteastory")
        {
            @Override
            public ItemStack getTabIconItem()
            {
                return new ItemStack(ItemLoader.tea_leaf);
            }
        };
    }
}
