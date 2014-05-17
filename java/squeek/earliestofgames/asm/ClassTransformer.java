package squeek.earliestofgames.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import squeek.earliestofgames.ModEarliestOfGames;

public class ClassTransformer implements IClassTransformer
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (name.equals("net.minecraft.block.BlockDynamicLiquid") || name.equals("ajd"))
		{
			boolean isObfuscated = name.equals("ajd");
			ModEarliestOfGames.Log.info("Patching BlockDynamicLiquid...");

			ClassNode classNode = readClassFromBytes(bytes);
			MethodNode methodNode = findMethodNodeOfClass(classNode, isObfuscated ? "h" : "func_149813_h", isObfuscated ? "(Lafn;IIII)V" : "(Lnet/minecraft/world/World;IIII)V");
			if (methodNode != null)
			{
				addHandleFlowIntoBlockHook(methodNode, Hooks.class, "handleFlowIntoBlock", "(Lnet/minecraft/world/World;IIII)Z");
				return writeClassToBytes(classNode);
			}
		}
		
		return bytes;
	}
	
	private void addHandleFlowIntoBlockHook(MethodNode method, Class<?> hookClass, String hookMethod, String hookDesc)
	{
		AbstractInsnNode targetNode = findFirstInstructionOfType(method, ALOAD);
		
		InsnList toInject = new InsnList();
	}

	private ClassNode readClassFromBytes(byte[] bytes)
	{
		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);
		return classNode;
	}

	private byte[] writeClassToBytes(ClassNode classNode)
	{
		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
		classNode.accept(writer);
		return writer.toByteArray();
	}

	private MethodNode findMethodNodeOfClass(ClassNode classNode, String methodName, String methodDesc)
	{
		for (MethodNode method : classNode.methods)
		{
			if (method.name.equals(methodName) && method.desc.equals(methodDesc))
			{
				ModEarliestOfGames.Log.info(" Found target method: " + methodName);
				return method;
			}
		}
		return null;
	}

	private AbstractInsnNode findFirstInstructionOfType(MethodNode method, int bytecode)
	{
		for (AbstractInsnNode instruction : method.instructions.toArray())
		{
			if (instruction.getOpcode() == bytecode)
				return instruction;
		}
		return null;
	}
}
