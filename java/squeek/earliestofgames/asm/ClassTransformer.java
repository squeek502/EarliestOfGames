package squeek.earliestofgames.asm;

import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformer implements IClassTransformer
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (name.equals("net.minecraft.item.ItemStack") || name.equals("ye"))
		{
			boolean isObfuscated = name.equals("ye");
			ModSpiceOfLife.Log.info("Patching ItemStack...");

			ClassNode classNode = readClassFromBytes(bytes);
			MethodNode methodNode = findMethodNodeOfClass(classNode, isObfuscated ? "b" : "onFoodEaten", isObfuscated ? "(Labw;Luf;)Lye;" : "(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;");
			if (methodNode != null)
			{
				addOnEatenHook(methodNode, HookOnFoodEaten.class, "onFoodEaten", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/EntityPlayer;)V");
				return writeClassToBytes(classNode);
			}
		}
		
		return bytes;
	}
}
