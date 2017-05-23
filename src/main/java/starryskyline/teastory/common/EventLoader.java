package starryskyline.teastory.common;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.oredict.OreDictionary;
import starryskyline.teastory.TeaStory;
import starryskyline.teastory.achievement.AchievementLoader;
import starryskyline.teastory.block.BlockLoader;
import starryskyline.teastory.item.ItemLoader;
import starryskyline.teastory.potion.PotionLoader;

public class EventLoader
{
    public static final EventBus EVENT_BUS = new EventBus();

    public EventLoader()
    {
        MinecraftForge.EVENT_BUS.register(this);
        EventLoader.EVENT_BUS.register(this);
    }
    
    @SubscribeEvent
    public void onDrops(net.minecraftforge.event.world.BlockEvent.BreakEvent event)
    {
    	if(!event.getWorld().isRemote)
    	{
            Block theblock = event.getState().getBlock();
            if(theblock == Blocks.LEAVES || theblock == Blocks.LEAVES2 || OreDictionary.getOres("treeLeaves").contains(new ItemStack(theblock)))
            {
                BlockLeaves leaves = (BlockLeaves)theblock;
                if(event.getState().getValue(PropertyBool.create("decayable")))
                {
                	int rand = event.getWorld().rand.nextInt(200);
                	if(rand == 0)
                	{
                        EntityItem entityitem = new EntityItem(event.getWorld(), (double)event.getPos().getX(), (double)event.getPos().getY(), (double)event.getPos().getZ(), new ItemStack(ItemLoader.tea_seeds, 1));
                        event.getWorld().spawnEntity(entityitem);
                	}
                	else if(rand >= 190)
                	{
                		EntityItem entityitem = new EntityItem(event.getWorld(), (double)event.getPos().getX(), (double)event.getPos().getY(), (double)event.getPos().getZ(), new ItemStack(ItemLoader.broken_tea, 1));
                        event.getWorld().spawnEntity(entityitem);
                	}
                }
            }
    	}
    }
    
    @SubscribeEvent
    public void onDrops2(net.minecraftforge.event.world.BlockEvent.HarvestDropsEvent event)
    {
        if(!event.getWorld().isRemote)
        {
            Block theblock = event.getState().getBlock();
            if(theblock == Blocks.LEAVES || theblock == Blocks.LEAVES2 || OreDictionary.getOres("treeLeaves").contains(new ItemStack(theblock)))
            {
                BlockLeaves leaves = (BlockLeaves)theblock;
                if(event.getState().getValue(PropertyBool.create("decayable")))
                {
                	int rand = event.getWorld().rand.nextInt(200);
                	if(rand == 0)
                	{
                        EntityItem entityitem = new EntityItem(event.getWorld(), (double)event.getPos().getX(), (double)event.getPos().getY(), (double)event.getPos().getZ(), new ItemStack(ItemLoader.tea_seeds, 1));
                        event.getWorld().spawnEntity(entityitem);
                	}
                	else if(rand >=190)
                	{
                		EntityItem entityitem = new EntityItem(event.getWorld(), (double)event.getPos().getX(), (double)event.getPos().getY(), (double)event.getPos().getZ(), new ItemStack(ItemLoader.broken_tea, 1));
                        event.getWorld().spawnEntity(entityitem);
                	}
                }
            }
        }
    }
    
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event)
    {
        if (!event.getEntityLiving().world.isRemote)
        {
        	PotionEffect effect1 = event.getEntityLiving().getActivePotionEffect(PotionLoader.PotionAgility);
            if (effect1 != null)
            {
                if (event.getEntityLiving().getRNG().nextFloat() < ((effect1.getAmplifier() + 1) * 0.15F + 0.05F))
                {
                    event.setAmount(0);
                }
            }
            /*else
            {
            	PotionEffect effect2 = event.entityLiving.getActivePotionEffect(PotionLoader.PotionCalmness);
                if (effect2 != null)
                {
                    if (event.entityLiving.getRNG().nextFloat() < (effect2.getAmplifier() + 1) * 0.1F)
                    {
                        event.ammount /= 2;
                    }
                }
            }*/
        }
    }
    
    @SubscribeEvent
    public void onPlayerAttack(AttackEntityEvent event)
    {
    	if (!event.getEntityLiving().world.isRemote)
    	{
        	PotionEffect effect = event.getEntityPlayer().getActivePotionEffect(PotionLoader.PotionLifeDrain);
        	if (effect != null)
            {
        		int level = effect.getAmplifier() + 1;
            	float r = event.getEntityPlayer().getRNG().nextFloat();
            	float health = event.getEntityPlayer().getHealth();
                if (health < event.getEntityPlayer().getMaxHealth() && r <= level*0.2F)
                {
                	event.getEntityPlayer().heal(4.0F - 6.0F/(level + 1.0F));
                }
            }
    	}
    }
    
    @SubscribeEvent
    public void onPlayerItemCrafted(PlayerEvent.ItemCraftedEvent event)
    {
        if (event.crafting.getItem() == ItemLoader.tea_leaf)
        {
            event.player.addStat(AchievementLoader.teaLeaf);
        }
        else if(event.crafting == new ItemStack(ItemLoader.cup, 1, 0))
        {
        	event.player.addStat(AchievementLoader.woodenCup);
        }
        else if((event.crafting.getItem() == ItemLoader.cup) && event.crafting.getItemDamage() == 0)
        {
        	event.player.addStat(AchievementLoader.woodenCup);
        }
        else if((event.crafting.getItem() == ItemLoader.cup) && event.crafting.getItemDamage() == 1)
        {
        	event.player.addStat(AchievementLoader.stoneCup);
        }
        else if((event.crafting.getItem() == ItemLoader.cup) && event.crafting.getItemDamage() == 2)
        {
        	event.player.addStat(AchievementLoader.glassCup);
        }
    }
    
    @SubscribeEvent
    public void onPlayerItemSmelted(PlayerEvent.ItemSmeltedEvent event)
    {
        if(event.smelting.getItem() == ItemLoader.burnt_tea)
        {
            event.player.addStat(AchievementLoader.burntLeaf);
        }
        else if(event.smelting.getItem() == new ItemStack(BlockLoader.empty_kettle, 1, 0).getItem() && event.smelting.getItemDamage() == 0)
        {
            event.player.addStat(AchievementLoader.kettle);
        }
        else if((event.smelting.getItem() == ItemLoader.cup) && event.smelting.getItemDamage() == 3)
        {
        	event.player.addStat(AchievementLoader.porcelainCup);
        }
    }
    
    @SubscribeEvent
    public void onPlayerItemPickedup(PlayerEvent.ItemPickupEvent event)
    {
    	if(event.pickedUp.getEntityItem().getItem() == ItemLoader.tea_seeds)
    	{
    		event.player.addStat(AchievementLoader.teaSeeds);
    	}
    }
    
    @SubscribeEvent
    public void onPlayerLogged(PlayerEvent.PlayerLoggedInEvent event)
    {
    	if(ConfigLoader.info)
    	{
    	    event.player.sendMessage(new TextComponentTranslation("teastory.info.welcome.1", "\u00a7a" + TeaStory.VERSION));
    	}
    }
}
