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
			}
			
			patchBlockBlocksFlowCalls(classNode, isObfuscated);
			
			return writeClassToBytes(classNode);
		}
		
		return bytes;
	}
	
	private void addHandleFlowIntoBlockHook(MethodNode method, Class<?> hookClass, String hookMethod, String hookDesc)
	{
		AbstractInsnNode targetNode = findFirstInstructionOfType(method, ALOAD);
		
		InsnList toInject = new InsnList();

		/*
		// equivalent to:
		if (Hooks.handleFlowIntoBlock(null, 0, 0, 0, 0))
			return;
		*/
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
	
	private boolean isMethodNodeOfBlockBlocksFlow(MethodInsnNode methodNode, boolean isObfuscated)
	{
		return methodNode.name.equals(isObfuscated ? "p" : "func_149807_p") && methodNode.desc.equals(isObfuscated ? "(Lafn;III)Z" : "(Lnet/minecraft/world/World;III)Z");
	}
	
	private void patchBlockBlocksFlowCalls(ClassNode classNode, boolean isObfuscated)
	{
		// blockBlocksFlow = func_149807_p
		
		MethodNode method;
		
		// blockBlocksFlow
		method = findMethodNodeOfClass(classNode, isObfuscated ? "p" : "func_149807_p", isObfuscated ? "(Lafn;III)Z" : "(Lnet/minecraft/world/World;III)Z");
		if (method != null)
		{
			method.access = ACC_PUBLIC;
		}
		
		// getOptimalFlowDirections
		method = findMethodNodeOfClass(classNode, isObfuscated ? "o" : "func_149808_o", isObfuscated ? "(Lafn;III)[Z" : "(Lnet/minecraft/world/World;III)[Z");
		if (method != null)
		{
			MethodInsnNode invokeSpecial = (MethodInsnNode) findFirstInstructionOfType(method, INVOKESPECIAL);
			while (invokeSpecial != null && !(isMethodNodeOfBlockBlocksFlow(invokeSpecial, isObfuscated)))
			{
				invokeSpecial = (MethodInsnNode) findNextInstructionOfType(invokeSpecial, INVOKESPECIAL);
			}
			if (invokeSpecial != null)
			{
				/*
				// equivalent to:
				Hooks.doesFlowGetBlockedBy(null, null, 0, 0, 0, (l+2) % 4 + 2);
				*/
				
				InsnList toInject = new InsnList();
				
				LocalVariableNode lVar = findLocalVariable(method, "l", "I");
			    
				toInject.add(new VarInsnNode(ILOAD, lVar.index)); 	// l
				toInject.add(new InsnNode(ICONST_2)); 				// 2
				toInject.add(new InsnNode(IADD)); 					// l+2
				toInject.add(new InsnNode(ICONST_4)); 				// 4
				toInject.add(new InsnNode(IREM)); 					// (l+2) % 4
				toInject.add(new InsnNode(ICONST_2)); 				// 2
				toInject.add(new InsnNode(IADD)); 					// (l+2) % 4 + 2
				
				method.instructions.insertBefore(invokeSpecial, toInject);
				
				invokeSpecial.setOpcode(INVOKESTATIC);
				invokeSpecial.owner = Hooks.class.getName().replace('.', '/');
				invokeSpecial.name = "doesFlowGetBlockedBy";
				invokeSpecial.desc = "(Lnet/minecraft/block/BlockDynamicLiquid;Lnet/minecraft/world/World;IIII)Z";
				
				//method.instructions.insert(invokeSpecial, new InsnNode(POP));
				
				ModEarliestOfGames.Log.info(" Patched 1 call of func_149807_p in " + method.name);
			}
		}
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
		ModEarliestOfGames.Log.error(" Target method not found: " + methodName);
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

	private AbstractInsnNode findNextInstructionOfType(AbstractInsnNode startInstruction, int bytecode)
	{
		AbstractInsnNode instruction = startInstruction.getNext();
		while (instruction != null)
		{
			if (instruction.getOpcode() == bytecode)
				return instruction;
			
			instruction = startInstruction.getNext();
		}
		return null;
	}
	
	private LocalVariableNode findLocalVariable(MethodNode method, String name, String desc)
	{
		for (LocalVariableNode localVar : method.localVariables)
		{
			if (localVar != null && localVar.name.equals(name) && localVar.desc.equals(desc))
				return localVar;
		}
		return null;
	}
}
