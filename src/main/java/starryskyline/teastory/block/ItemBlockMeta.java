package starryskyline.teastory.block;

import com.google.common.base.Function;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;

public class ItemBlockMeta extends ItemMultiTexture
{
	public ItemBlockMeta(Block block, Block block2, Function<ItemStack, String> nameFunction)
    {
		super(block, block, nameFunction::apply); //TODO Guava's Function returns @Nullable type
	}

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName() + this.nameFunction.apply(stack);
    }
}
