package squeek.earliestofgames.asm;

import static org.objectweb.asm.Opcodes.*;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
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
				addHandleFlowIntoBlockHook(methodNode, Hooks.class, "handleFlowIntoBlock", "(Lnet/minecraft/block/BlockDynamicLiquid;Lnet/minecraft/world/World;IIII)Z");
				return writeClassToBytes(classNode);
			}
		}
		
		return bytes;
	}
	
	private void addHandleFlowIntoBlockHook(MethodNode method, Class<?> hookClass, String hookMethod, String hookDesc)
	{
		// equivalent to:
		if (Hooks.handleFlowIntoBlock(null, 0, 0, 0, 0))
			return;
		
		AbstractInsnNode targetNode = findFirstInstructionOfType(method, ALOAD);
		
		InsnList toInject = new InsnList();

		toInject.add(new VarInsnNode(ALOAD, 0)); 	// this
		toInject.add(new VarInsnNode(ALOAD, 1)); 	// world
		toInject.add(new VarInsnNode(ILOAD, 2)); 	// x
		toInject.add(new VarInsnNode(ILOAD, 3)); 	// y
		toInject.add(new VarInsnNode(ILOAD, 4)); 	// z
		toInject.add(new VarInsnNode(ILOAD, 5)); 	// newFlowDecay
		toInject.add(new MethodInsnNode(INVOKESTATIC, hookClass.getName().replace('.', '/'), hookMethod, hookDesc));
		LabelNode label = new LabelNode();
		toInject.add(new JumpInsnNode(IFEQ, label));
		toInject.add(new InsnNode(RETURN));
		toInject.add(label);
		
		method.instructions.insertBefore(targetNode, toInject);

		ModEarliestOfGames.Log.info(" Patched " + method.name);
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
