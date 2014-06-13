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
import com.sun.xml.internal.ws.org.objectweb.asm.Type;
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

			patchBlockLiquid(classNode, isObfuscated);

			return writeClassToBytes(classNode);
		}
		else if (name.equals("net.minecraft.world.World") || name.equals("afn"))
		{
			boolean isObfuscated = name.equals("afn");

			ClassNode classNode = readClassFromBytes(bytes);

			patchWorld(classNode, isObfuscated);

			return writeClassToBytes(classNode);
		}

		return bytes;
	}

	public boolean patchWorld(ClassNode classNode, boolean isObfuscated)
	{
		ModEarliestOfGames.Log.info("Patching World...");

		MethodNode method;

		method = findMethodNodeOfClass(classNode, isObfuscated ? "q" : "func_147469_q", "(III)Z");
		if (method != null)
		{
			LabelNode end = findEndLabel(method);
			AbstractInsnNode targetNode = findFirstInstructionOfType(method, ALOAD);

			InsnList toInject = new InsnList();

			/*
			// equivalent to:
			int isBlockFullCube = Hooks.isBlockFullCube(null, 0, 0, 0);
			if (isBlockFullCube != -1)
				return isBlockFullCube != 0;
			*/

			LabelNode varStartLabel = new LabelNode();
			LocalVariableNode localVar = new LocalVariableNode("isBlockFullCube", "I", method.signature, varStartLabel, end, method.maxLocals);
			method.maxLocals += Type.INT_TYPE.getSize();
			method.localVariables.add(localVar);

			toInject.add(new VarInsnNode(ALOAD, 0)); 					// this
			toInject.add(new VarInsnNode(ILOAD, 1)); 					// x
			toInject.add(new VarInsnNode(ILOAD, 2)); 					// y
			toInject.add(new VarInsnNode(ILOAD, 3)); 					// z
			toInject.add(new MethodInsnNode(INVOKESTATIC, Hooks.class.getName().replace('.', '/'), "isBlockFullCube", "(Lnet/minecraft/world/World;III)I"));
			toInject.add(new VarInsnNode(ISTORE, localVar.index));		// isBlockFullCube = Hooks.isBlockFullCube(...)
			toInject.add(varStartLabel);								// variable scope start
			LabelNode label = new LabelNode();							// label if condition is true
			toInject.add(new VarInsnNode(ILOAD, localVar.index));		// isBlockFullCube
			toInject.add(new InsnNode(ICONST_M1));						// -1
			toInject.add(new JumpInsnNode(IF_ICMPEQ, label));			// isBlockFullCube != -1
			LabelNode labelReturnIf = new LabelNode();					// label if second condition is true
			toInject.add(new VarInsnNode(ILOAD, localVar.index));		// isBlockFullCube
			toInject.add(new JumpInsnNode(IFEQ, labelReturnIf));		// isBlockFullCube != 0
			toInject.add(new InsnNode(ICONST_1));						// 1 (true)
			toInject.add(new InsnNode(IRETURN));						// return true;
			toInject.add(labelReturnIf);								// if isBlockFullCube == 0, jump here
			toInject.add(new InsnNode(ICONST_0));						// 0 (false)
			toInject.add(new InsnNode(IRETURN));						// return false;
			toInject.add(label);										// if isBlockFullCube == -1, jump here

			method.instructions.insertBefore(targetNode, toInject);

			ModEarliestOfGames.Log.info(" Patched " + method.name);
		}
		return false;
	}

	private void patchBlockLiquid(ClassNode classNode, boolean isObfuscated)
	{
		ModEarliestOfGames.Log.info("Patching BlockLiquid...");

		MethodNode method;

		method = findMethodNodeOfClass(classNode, isObfuscated ? "e" : "func_149804_e", isObfuscated ? "(Lafn;III)I" : "(Lnet/minecraft/world/World;III)I");
		if (method != null)
		{
			//LabelNode start = findStartLabel(method);
			LabelNode end = findEndLabel(method);
			AbstractInsnNode targetNode = findFirstInstructionOfType(method, ALOAD);

			InsnList toInject = new InsnList();

			/*
			// equivalent to:
			int flowDecay = Hooks.getFlowDecay(null, null, 0, 0, 0);
			if (flowDecay >= 0)
				return flowDecay;
			*/

			LabelNode varStartLabel = new LabelNode();
			LocalVariableNode localVar = new LocalVariableNode("flowDecay", "I", method.signature, varStartLabel, end, method.maxLocals);
			method.maxLocals += Type.INT_TYPE.getSize();
			method.localVariables.add(localVar);

			toInject.add(new VarInsnNode(ALOAD, 0)); 					// this
			toInject.add(new VarInsnNode(ALOAD, 1)); 					// world
			toInject.add(new VarInsnNode(ILOAD, 2)); 					// x
			toInject.add(new VarInsnNode(ILOAD, 3)); 					// y
			toInject.add(new VarInsnNode(ILOAD, 4)); 					// z
			toInject.add(new MethodInsnNode(INVOKESTATIC, Hooks.class.getName().replace('.', '/'), "getFlowDecay", "(Lnet/minecraft/block/BlockLiquid;Lnet/minecraft/world/World;III)I"));
			toInject.add(new VarInsnNode(ISTORE, localVar.index));		// flowDecay = return val
			toInject.add(varStartLabel);								// variable scope start
			LabelNode label = new LabelNode();							// label if condition is true
			toInject.add(new VarInsnNode(ILOAD, localVar.index));		// check against flowDecay
			toInject.add(new JumpInsnNode(IFLT, label));				// flowDecay >= 0
			toInject.add(new VarInsnNode(ILOAD, localVar.index));		// get ready to return flowDecay
			toInject.add(new InsnNode(IRETURN));						// return flowDecay
			toInject.add(label);										// if condition was true, jump here

			method.instructions.insertBefore(targetNode, toInject);

			ModEarliestOfGames.Log.info(" Patched " + method.name);
		}

		method = findMethodNodeOfClass(classNode, isObfuscated ? "e" : "getEffectiveFlowDecay", isObfuscated ? "(Lafx;III)I" : "(Lnet/minecraft/world/IBlockAccess;III)I");
		if (method != null)
		{
			//LabelNode start = findStartLabel(method);
			LabelNode end = findEndLabel(method);
			AbstractInsnNode targetNode = findFirstInstructionOfType(method, ALOAD);

			InsnList toInject = new InsnList();

			/*
			// equivalent to:
			int flowDecay = Hooks.getFlowDecay(null, null, 0, 0, 0);
			if (flowDecay >= 0)
				return flowDecay;
			*/

			ModEarliestOfGames.Log.debug("  maxlocals before: " + method.maxLocals);
			LabelNode varStartLabel = new LabelNode();
			LocalVariableNode localVar = new LocalVariableNode("flowDecay", "I", method.signature, varStartLabel, end, method.maxLocals);
			method.maxLocals += Type.INT_TYPE.getSize();
			method.localVariables.add(localVar);
			ModEarliestOfGames.Log.debug("  maxlocals after: " + method.maxLocals);

			toInject.add(new VarInsnNode(ALOAD, 0)); 					// this
			toInject.add(new VarInsnNode(ALOAD, 1)); 					// world
			toInject.add(new VarInsnNode(ILOAD, 2)); 					// x
			toInject.add(new VarInsnNode(ILOAD, 3)); 					// y
			toInject.add(new VarInsnNode(ILOAD, 4)); 					// z
			toInject.add(new MethodInsnNode(INVOKESTATIC, Hooks.class.getName().replace('.', '/'), "getEffectiveFlowDecay", "(Lnet/minecraft/block/BlockLiquid;Lnet/minecraft/world/IBlockAccess;III)I"));
			toInject.add(new VarInsnNode(ISTORE, localVar.index));		// flowDecay = return val
			toInject.add(varStartLabel);								// variable scope start
			LabelNode label = new LabelNode();							// label if condition is true
			toInject.add(new VarInsnNode(ILOAD, localVar.index));		// check against flowDecay
			toInject.add(new JumpInsnNode(IFLT, label));				// flowDecay >= 0
			toInject.add(new VarInsnNode(ILOAD, localVar.index));		// get ready to return flowDecay
			toInject.add(new InsnNode(IRETURN));						// return flowDecay
			toInject.add(label);										// if condition was true, jump here

			method.instructions.insertBefore(targetNode, toInject);

			ModEarliestOfGames.Log.info(" Patched " + method.name);
		}
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

			// getSmallestFlowDecay calls
			MethodInsnNode invoke = (MethodInsnNode) findFirstInstructionOfType(method, INVOKEVIRTUAL);
			for (int i = 0; i < 4; i++)
			{
				while (invoke != null && !isMethodNodeOfGetSmallestFlowDecay(invoke, isObfuscated))
				{
					invoke = (MethodInsnNode) findNextInstructionOfType(invoke, INVOKEVIRTUAL);
				}
				
				toInject.clear();
				
				int flowDir = (i + 2) % 4 + 2;
				int byteCode = flowDir == 2 ? ICONST_2 : (flowDir == 3 ? ICONST_3 : (flowDir == 4 ? ICONST_4 : ICONST_5));
				
				toInject.add(new InsnNode(byteCode));

				patchGetSmallestFlowDecayCall(method, invoke, toInject);
			}
			
			// getFlowDecay calls
			for (int i = 0; i < 2; i++)
			{
				do
				{
					invoke = (MethodInsnNode) findNextInstructionOfType(invoke, INVOKEVIRTUAL);
				}
				while (invoke != null && !isMethodNodeOfGetFlowDecay(invoke, isObfuscated));

				toInject.clear();
				toInject.add(new InsnNode(ICONST_1)); // up

				patchGetFlowDecayCall(method, invoke, toInject);
			}
			
			// liquidCanDisplaceBlock call
			do
			{
				invoke = (MethodInsnNode) findNextInstructionOfType(invoke, INVOKESPECIAL);
			}
			while (invoke != null && !isMethodNodeOfLiquidCanDisplaceBlock(invoke, isObfuscated));

			toInject = new InsnList();
			toInject.add(new InsnNode(ICONST_0));

			patchLiquidCanDisplaceBlockCall(method, invoke, toInject);

			// flowIntoBlock calls
			for (int i = 0; i < 2; i++)
			{
				do
				{
					invoke = (MethodInsnNode) findNextInstructionOfType(invoke, INVOKESPECIAL);
				}
				while (invoke != null && !isMethodNodeOfFlowIntoBlock(invoke, isObfuscated));

				toInject.clear();
				toInject.add(new InsnNode(ICONST_0));

				patchFlowIntoBlockCall(method, invoke, toInject);

				method.instructions.insert(invoke, new InsnNode(POP));
			}

			// blockBlocksFlow call
			do
			{
				invoke = (MethodInsnNode) findNextInstructionOfType(invoke, INVOKESPECIAL);
			}
			while (invoke != null && !isMethodNodeOfBlockBlocksFlow(invoke, isObfuscated));

			toInject = new InsnList();
			toInject.add(new InsnNode(ICONST_0));

			patchBlockBlocksFlowCall(method, invoke, toInject);

			// more flowIntoBlock calls
			for (int i = 0; i < 4; i++)
			{
				do
				{
					invoke = (MethodInsnNode) findNextInstructionOfType(invoke, INVOKESPECIAL);
				}
				while (invoke != null && !isMethodNodeOfFlowIntoBlock(invoke, isObfuscated));

				toInject.clear();

				int flowDir = (i + 2) % 4 + 2;
				int byteCode = flowDir == 2 ? ICONST_2 : (flowDir == 3 ? ICONST_3 : (flowDir == 4 ? ICONST_4 : ICONST_5));
				toInject.add(new InsnNode(byteCode));

				patchFlowIntoBlockCall(method, invoke, toInject);

				method.instructions.insert(invoke, new InsnNode(POP));
			}
		}

		// liquidCanDisplaceBlock
		method = findMethodNodeOfClass(classNode, isObfuscated ? "q" : "func_149809_q", isObfuscated ? "(Lafn;III)Z" : "(Lnet/minecraft/world/World;III)Z");
		if (method != null)
		{
			AbstractInsnNode targetNode = findFirstInstructionOfType(method, ALOAD);

			InsnList toInject = new InsnList();
			/*
			// equivalent to:
			if (Hooks.canLiquidDisplaceBlock(this, null, 0, 0, 0))
				return true;
			*/
			toInject.add(new VarInsnNode(ALOAD, 0)); 	// this
			toInject.add(new VarInsnNode(ALOAD, 1)); 	// world
			toInject.add(new VarInsnNode(ILOAD, 2)); 	// x
			toInject.add(new VarInsnNode(ILOAD, 3)); 	// y
			toInject.add(new VarInsnNode(ILOAD, 4)); 	// z
			toInject.add(new MethodInsnNode(INVOKESTATIC, Hooks.class.getName().replace('.', '/'), "canLiquidDisplaceBlock", "(Lnet/minecraft/block/BlockDynamicLiquid;Lnet/minecraft/world/World;III)Z"));
			LabelNode label = new LabelNode();
			toInject.add(new JumpInsnNode(IFEQ, label));
			toInject.add(new InsnNode(ICONST_1));
			toInject.add(new InsnNode(IRETURN));
			toInject.add(label);

			method.instructions.insertBefore(targetNode, toInject);

			ModEarliestOfGames.Log.info(" Patched " + method.name);
		}
	}

	private boolean isMethodNodeOfLiquidCanDisplaceBlock(MethodInsnNode methodNode, boolean isObfuscated)
	{
		boolean isRightName = methodNode.name.equals(isObfuscated ? "q" : "func_149809_q");
		boolean isRightDesc = methodNode.desc.equals(isObfuscated ? "(Lafn;III)Z" : "(Lnet/minecraft/world/World;III)Z");
		return isRightName && isRightDesc;
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
	
	private boolean isMethodNodeOfGetSmallestFlowDecay(MethodInsnNode methodNode, boolean isObfuscated)
	{
		boolean isRightName = methodNode.name.equals(isObfuscated ? "a" : "func_149810_a");
		boolean isRightDesc = methodNode.desc.equals(isObfuscated ? "(Lafn;IIII)I" : "(Lnet/minecraft/world/World;IIII)I");
		return isRightName && isRightDesc;
	}
	
	private boolean isMethodNodeOfGetFlowDecay(MethodInsnNode methodNode, boolean isObfuscated)
	{
		boolean isRightName = methodNode.name.equals(isObfuscated ? "e" : "func_149804_e");
		boolean isRightDesc = methodNode.desc.equals(isObfuscated ? "(Lafn;III)I" : "(Lnet/minecraft/world/World;III)I");
		return isRightName && isRightDesc;
	}

	private void patchLiquidCanDisplaceBlockCall(MethodNode method, MethodInsnNode invokeInstruction, InsnList additionalInstructions)
	{
		if (invokeInstruction != null)
		{
			if (additionalInstructions.size() > 0)
				method.instructions.insertBefore(invokeInstruction, additionalInstructions);

			invokeInstruction.setOpcode(INVOKESTATIC);
			invokeInstruction.owner = Hooks.class.getName().replace('.', '/');
			invokeInstruction.name = "canLiquidDisplaceBlockFrom";
			invokeInstruction.desc = "(Lnet/minecraft/block/BlockDynamicLiquid;Lnet/minecraft/world/World;IIII)Z";

			ModEarliestOfGames.Log.info("  Patched call of liquidCanDisplaceBlock in " + method.name);
		}
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
	
	private void patchGetSmallestFlowDecayCall(MethodNode method, MethodInsnNode invokeInstruction, InsnList additionalInstructions)
	{
		if (invokeInstruction != null)
		{
			if (additionalInstructions.size() > 0)
				method.instructions.insertBefore(invokeInstruction, additionalInstructions);

			invokeInstruction.setOpcode(INVOKESTATIC);
			invokeInstruction.owner = Hooks.class.getName().replace('.', '/');
			invokeInstruction.name = "getSmallestFlowDecayTo";
			invokeInstruction.desc = "(Lnet/minecraft/block/BlockDynamicLiquid;Lnet/minecraft/world/World;IIIII)I";

			ModEarliestOfGames.Log.info("  Patched call of getSmallestFlowDecay in " + method.name);
		}
	}
	
	private void patchGetFlowDecayCall(MethodNode method, MethodInsnNode invokeInstruction, InsnList additionalInstructions)
	{
		if (invokeInstruction != null)
		{
			if (additionalInstructions.size() > 0)
				method.instructions.insertBefore(invokeInstruction, additionalInstructions);

			invokeInstruction.setOpcode(INVOKESTATIC);
			invokeInstruction.owner = Hooks.class.getName().replace('.', '/');
			invokeInstruction.name = "getFlowDecayTo";
			invokeInstruction.desc = "(Lnet/minecraft/block/BlockDynamicLiquid;Lnet/minecraft/world/World;IIII)I";

			ModEarliestOfGames.Log.info("  Patched call of getFlowDecay in " + method.name);
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

	private LabelNode findStartLabel(MethodNode method)
	{
		for (AbstractInsnNode instruction : method.instructions.toArray())
		{
			if (instruction instanceof LabelNode)
				return (LabelNode) instruction;
		}
		return null;
	}

	private LabelNode findEndLabel(MethodNode method)
	{
		LabelNode lastLabel = null;
		for (AbstractInsnNode instruction : method.instructions.toArray())
		{
			if (instruction instanceof LabelNode)
				lastLabel = (LabelNode) instruction;
		}
		return lastLabel;
	}
}
