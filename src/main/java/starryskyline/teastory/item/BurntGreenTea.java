package starryskyline.teastory.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import starryskyline.teastory.common.ConfigLoader;
import starryskyline.teastory.potion.PotionLoader;

public class BurntGreenTea extends ItemTeaDrink
{
	public BurntGreenTea()
    {
        super("burnt_green_tea");
    }

    @Override
	public void addInformation(ItemStack itemstack, EntityPlayer entityplayer, List<String> list, boolean b)
    {
        list.add(I18n.translateToLocal("teastory.tooltip.burnt_green_tea"));
    }

    @Override
    protected void onFoodEaten(ItemStack itemstack, World world, EntityPlayer entityplayer)
    {
        if(!world.isRemote)
        {
        	int tier = itemstack.getItemDamage();
        	if (tier == 0)
        	{
        		entityplayer.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, Math.max(0, ConfigLoader.TeaDrink_Time) / 2, 0)); 
        		if(world.rand.nextFloat() < 0.2F)
        		{
        			entityplayer.addPotionEffect(new PotionEffect(PotionLoader.PotionAgility, Math.max(0, ConfigLoader.TeaDrink_Time), 0));
        		}
        	}
        	else
        	{
        		entityplayer.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, (int)(Math.max(0, ConfigLoader.TeaDrink_Time) * (10 + tier) / 20), tier - 1)); 
        		if(world.rand.nextFloat() < 0.2F)
        		{
        			entityplayer.addPotionEffect(new PotionEffect(PotionLoader.PotionAgility, Math.max(0, ConfigLoader.TeaDrink_Time) * (10 + tier) / 10, tier - 1));
        		}
        	}
        }
    }
}
