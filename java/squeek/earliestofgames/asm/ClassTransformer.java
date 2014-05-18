package squeek.earliestofgames.asm;

import static org.objectweb.asm.Opcodes.*;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;
import squeek.earliestofgames.ModEarliestOfGames;

public class ClassTransformer implements IClassTransformer
{
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes)
	{
		if (name.equals("net.minecraft.block.BlockDynamicLiquid") || name.equals("ajd"))
		{
			boolean isObfuscated = name.equals("ajd");

			ClassNode classNode = readClassFromBytes(bytes);
			
			patchBlockDynamicLiquid(classNode, isObfuscated);
			
			return writeClassToBytes(classNode);
		}
		else if (name.equals("net.minecraft.block.BlockLiquid") || name.equals("aki"))
		{
			boolean isObfuscated = name.equals("aki");

			ClassNode classNode = readClassFromBytes(bytes);
			
			patchBlockDynamicLiquid(classNode, isObfuscated);
			
			return writeClassToBytes(classNode);
		}
		
		return bytes;
	}
	
	private int patchBlockLiquid(ClassNode classNode, boolean isObfuscated)
	{
		ModEarliestOfGames.Log.info("Patching BlockLiquid...");
		
		MethodNode method;
		
		method = findMethodNodeOfClass(classNode, isObfuscated ? "e" : "func_149804_e", isObfuscated ? "(Lafn;III)I" : "(Lnet/minecraft/world/World;III)I");
		if (method != null)
		{
			AbstractInsnNode targetNode = findFirstInstructionOfType(method, ALOAD);
			
			InsnList toInject = new InsnList();

			
			// equivalent to:
			int flowDecay = Hooks.getFlowDecay(null, null, 0, 0, 0);
			if (flowDecay >= 0)
				return flowDecay;

			LocalVariableNode localVar = new LocalVariableNode("flowDecay", "I", null, null, null, method.localVariables.size());
			method.localVariables.add(null);
			
			toInject.add(new VarInsnNode(ALOAD, 0)); 	// this
			toInject.add(new VarInsnNode(ALOAD, 1)); 	// world
			toInject.add(new VarInsnNode(ILOAD, 2)); 	// x
			toInject.add(new VarInsnNode(ILOAD, 3)); 	// y
			toInject.add(new VarInsnNode(ILOAD, 4)); 	// z
			toInject.add(new MethodInsnNode(INVOKESTATIC, Hooks.class.getName().replace('.', '/'), "getFlowDecay", "(Lnet/minecraft/block/BlockLiquid;Lnet/minecraft/world/World;III)I"));
			toInject.add(new VarInsnNode(ISTORE, localVar.index));
			LabelNode label = new LabelNode();
			toInject.add(new JumpInsnNode(IFEQ, label));
			toInject.add(new InsnNode(RETURN));
			toInject.add(label);
			
			method.instructions.insertBefore(targetNode, toInject);

			ModEarliestOfGames.Log.info(" Patched " + method.name);
		}
		return 0;
	}
	
	private void patchBlockDynamicLiquid(ClassNode classNode, boolean isObfuscated)
	{
		ModEarliestOfGames.Log.info("Patching BlockDynamicLiquid...");
		
		MethodNode method;
		
		// flowIntoBlock
		method = findMethodNodeOfClass(classNode, isObfuscated ? "h" : "func_149813_h", isObfuscated ? "(Lafn;IIII)V" : "(Lnet/minecraft/world/World;IIII)V");
		if (method != null)
		{
			method.access = ACC_PUBLIC;
			ModEarliestOfGames.Log.info("  Set " + method.name + " access level to public");
		}
		
		// blockBlocksFlow
		method = findMethodNodeOfClass(classNode, isObfuscated ? "p" : "func_149807_p", isObfuscated ? "(Lafn;III)Z" : "(Lnet/minecraft/world/World;III)Z");
		if (method != null)
		{
			method.access = ACC_PUBLIC;
			ModEarliestOfGames.Log.info("  Set " + method.name + " access level to public");
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

			InsnList toInject = new InsnList();
			
			LocalVariableNode lVar = findLocalVariable(method, "l", "I");

			/*
			// equivalent to:
			Hooks.doesFlowGetBlockedBy(null, null, 0, 0, 0, (l+2) % 4 + 2);
			*/
			toInject.add(new VarInsnNode(ILOAD, lVar.index)); 	// l
			toInject.add(new InsnNode(ICONST_2)); 				// 2
			toInject.add(new InsnNode(IADD)); 					// l+2
			toInject.add(new InsnNode(ICONST_4)); 				// 4
			toInject.add(new InsnNode(IREM)); 					// (l+2) % 4
			toInject.add(new InsnNode(ICONST_2)); 				// 2
			toInject.add(new InsnNode(IADD)); 					// (l+2) % 4 + 2
			
			patchBlockBlocksFlowCall(method, invokeSpecial, toInject);
			
			invokeSpecial = (MethodInsnNode) findNextInstructionOfType(invokeSpecial, INVOKESPECIAL);
			while (invokeSpecial != null && !(isMethodNodeOfBlockBlocksFlow(invokeSpecial, isObfuscated)))
			{
				invokeSpecial = (MethodInsnNode) findNextInstructionOfType(invokeSpecial, INVOKESPECIAL);
			}
			
			toInject.clear();
			toInject.add(new InsnNode(ICONST_0));

			patchBlockBlocksFlowCall(method, invokeSpecial, toInject);
		}
		
		// calculateFlowCost
		method = findMethodNodeOfClass(classNode, isObfuscated ? "c" : "func_149812_c", isObfuscated ? "(Lafn;IIIII)I" : "(Lnet/minecraft/world/World;IIIII)I");
		if (method != null)
		{
			MethodInsnNode invokeSpecial = (MethodInsnNode) findFirstInstructionOfType(method, INVOKESPECIAL);
			while (invokeSpecial != null && !(isMethodNodeOfBlockBlocksFlow(invokeSpecial, isObfuscated)))
			{
				invokeSpecial = (MethodInsnNode) findNextInstructionOfType(invokeSpecial, INVOKESPECIAL);
			}

			InsnList toInject = new InsnList();
			
			LocalVariableNode lVar = findLocalVariable(method, "k1", "I");

			/*
			// equivalent to:
			Hooks.doesFlowGetBlockedBy(null, null, 0, 0, 0, (k1+2) % 4 + 2);
			*/
			toInject.add(new VarInsnNode(ILOAD, lVar.index)); 	// l
			toInject.add(new InsnNode(ICONST_2)); 				// 2
			toInject.add(new InsnNode(IADD)); 					// k1+2
			toInject.add(new InsnNode(ICONST_4)); 				// 4
			toInject.add(new InsnNode(IREM)); 					// (k1+2) % 4
			toInject.add(new InsnNode(ICONST_2)); 				// 2
			toInject.add(new InsnNode(IADD)); 					// (k1+2) % 4 + 2
			
			patchBlockBlocksFlowCall(method, invokeSpecial, toInject);

			invokeSpecial = (MethodInsnNode) findNextInstructionOfType(invokeSpecial, INVOKESPECIAL);
			while (invokeSpecial != null && !(isMethodNodeOfBlockBlocksFlow(invokeSpecial, isObfuscated)))
			{
				invokeSpecial = (MethodInsnNode) findNextInstructionOfType(invokeSpecial, INVOKESPECIAL);
			}
			
			toInject.clear();
			toInject.add(new InsnNode(ICONST_0));

			patchBlockBlocksFlowCall(method, invokeSpecial, toInject);
		}
		
		// updateTick
		method = findMethodNodeOfClass(classNode, isObfuscated ? "a" : "updateTick", isObfuscated ? "(Lafn;IIILjava/util/Random;)V" : "(Lnet/minecraft/world/World;IIILjava/util/Random;)V");
		if (method != null)
		{
			InsnList toInject = new InsnList();
			
			// flowIntoBlock calls
			MethodInsnNode invokeSpecial = (MethodInsnNode) findFirstInstructionOfType(method, INVOKESPECIAL);
			for (int i=0; i<2; i++)
			{
				while (invokeSpecial != null && !(isMethodNodeOfFlowIntoBlock(invokeSpecial, isObfuscated)))
				{
					invokeSpecial = (MethodInsnNode) findNextInstructionOfType(invokeSpecial, INVOKESPECIAL);
				}
			
				toInject.clear();
				toInject.add(new InsnNode(ICONST_0));
			
				patchFlowIntoBlockCall(method, invokeSpecial, toInject);
				
				method.instructions.insert(invokeSpecial, new InsnNode(POP));
			}
			
			// blockBlocksFlow call
			invokeSpecial = (MethodInsnNode) findNextInstructionOfType(invokeSpecial, INVOKESPECIAL);
			while (invokeSpecial != null && !(isMethodNodeOfBlockBlocksFlow(invokeSpecial, isObfuscated)))
			{
				invokeSpecial = (MethodInsnNode) findNextInstructionOfType(invokeSpecial, INVOKESPECIAL);
			}

			toInject = new InsnList();
			toInject.add(new InsnNode(ICONST_0));

			patchBlockBlocksFlowCall(method, invokeSpecial, toInject);

			
			// more flowIntoBlock calls
			for (int i=0; i<4; i++)
			{
				invokeSpecial = (MethodInsnNode) findNextInstructionOfType(invokeSpecial, INVOKESPECIAL);
				while (invokeSpecial != null && !(isMethodNodeOfFlowIntoBlock(invokeSpecial, isObfuscated)))
				{
					invokeSpecial = (MethodInsnNode) findNextInstructionOfType(invokeSpecial, INVOKESPECIAL);
				}
				
				toInject.clear();
				
				int flowDir = (i+2) % 4 + 2;
				int byteCode = flowDir == 2 ? ICONST_2 : (flowDir == 3 ? ICONST_3 : (flowDir == 4 ? ICONST_4 : ICONST_5));
				toInject.add(new InsnNode(byteCode));
				
				patchFlowIntoBlockCall(method, invokeSpecial, toInject);
				
				method.instructions.insert(invokeSpecial, new InsnNode(POP));
			}
			
		}
	}
	
	private boolean isMethodNodeOfBlockBlocksFlow(MethodInsnNode methodNode, boolean isObfuscated)
	{
		boolean isRightName = methodNode.name.equals(isObfuscated ? "p" : "func_149807_p");
		boolean isRightDesc = methodNode.desc.equals(isObfuscated ? "(Lafn;III)Z" : "(Lnet/minecraft/world/World;III)Z");
		return isRightName && isRightDesc;
	}

	private boolean isMethodNodeOfFlowIntoBlock(MethodInsnNode methodNode, boolean isObfuscated)
	{
		boolean isRightName = methodNode.name.equals(isObfuscated ? "h" : "func_149813_h");
		boolean isRightDesc = methodNode.desc.equals(isObfuscated ? "(Lafn;IIII)V" : "(Lnet/minecraft/world/World;IIII)V");
		return isRightName && isRightDesc;
	}
	
	private void patchBlockBlocksFlowCall(MethodNode method, MethodInsnNode invokeInstruction, InsnList additionalInstructions)
	{
		if (invokeInstruction != null)
		{
			if (additionalInstructions.size() > 0)
				method.instructions.insertBefore(invokeInstruction, additionalInstructions);
			
			invokeInstruction.setOpcode(INVOKESTATIC);
			invokeInstruction.owner = Hooks.class.getName().replace('.', '/');
			invokeInstruction.name = "doesFlowGetBlockedBy";
			invokeInstruction.desc = "(Lnet/minecraft/block/BlockDynamicLiquid;Lnet/minecraft/world/World;IIII)Z";
			
			ModEarliestOfGames.Log.info("  Patched call of blockBlocksFlow in " + method.name);
		}
	}
	
	private void patchFlowIntoBlockCall(MethodNode method, MethodInsnNode invokeInstruction, InsnList additionalInstructions)
	{
		if (invokeInstruction != null)
		{
			if (additionalInstructions.size() > 0)
				method.instructions.insertBefore(invokeInstruction, additionalInstructions);
			
			invokeInstruction.setOpcode(INVOKESTATIC);
			invokeInstruction.owner = Hooks.class.getName().replace('.', '/');
			invokeInstruction.name = "onFlowIntoBlockFrom";
			invokeInstruction.desc = "(Lnet/minecraft/block/BlockDynamicLiquid;Lnet/minecraft/world/World;IIIII)Z";
			
			ModEarliestOfGames.Log.info("  Patched call of flowIntoBlocks in " + method.name);
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
		if (startInstruction != null)
		{
			AbstractInsnNode instruction = startInstruction.getNext();
			while (instruction != null)
			{
				if (instruction.getOpcode() == bytecode)
					return instruction;
				
				instruction = instruction.getNext();
			}
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
