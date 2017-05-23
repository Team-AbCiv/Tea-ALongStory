package starryskyline.teastory.creativetab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import starryskyline.teastory.item.ItemLoader;

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
