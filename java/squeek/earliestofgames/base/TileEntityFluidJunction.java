package squeek.earliestofgames.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import squeek.earliestofgames.ModEarliestOfGames;
import squeek.earliestofgames.asm.Hooks;
import squeek.earliestofgames.helpers.FluidHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityFluidJunction extends TileEntity implements IFluidFlowHandler
{
	FluidFlow inputFlows[] = new FluidFlow[ForgeDirection.VALID_DIRECTIONS.length];
	FluidFlow outputFlows[] = new FluidFlow[ForgeDirection.VALID_DIRECTIONS.length];
	FluidFlow masterFlow = null;

	public TileEntityFluidJunction()
	{
		ModEarliestOfGames.Log.info("");
		ModEarliestOfGames.Log.info("");
		ModEarliestOfGames.Log.info("TileEntityFluidJunction constructor");
	}
	
	/**
	 * Determines if the given fluid is able to flow into the given side of the block
	 * 
	 * @param fluid The fluid type that is trying to flow into the block
	 * @param side The side of the block that the fluid is trying to flow into
	 * @return True if the fluid can flow into the given side
	 */
	public boolean canFluidFlowIntoSide(Fluid fluid, ForgeDirection side)
	{
		if (getOutputFlow(side) != null)
		{
			ModEarliestOfGames.Log.info("can not flow into side " + side + " because output is not null");
		}
		return getOutputFlow(side) == null;
	}

	/**
	 * Determines if the given fluid is able to flow out of the given side of the block
	 * 
	 * @param fluid The fluid type that is trying to flow out of the block
	 * @param side The side of the block that the fluid is trying to flow out of
	 * @return True if the fluid can flow out of the given side
	 */
	public boolean canFluidFlowOutOfSide(Fluid fluid, ForgeDirection side)
	{
		if (getInputFlow(side) != null)
		{
			ModEarliestOfGames.Log.info("can not flow out of side " + side + " because input is not null");
		}
		return side != ForgeDirection.UP && getInputFlow(side) == null;
	}

	@Override
	public boolean doesFlowGetBlockedBySide(Fluid fluid, ForgeDirection side)
	{
		return !canFluidFlowIntoSide(fluid, side);
	}

	@Override
	public boolean handleFlowIntoBlock(Fluid fluid, int newFlowDecay, ForgeDirection side)
	{
		ModEarliestOfGames.Log.info("onFlowIntoBlock: " + newFlowDecay + " side: " + side);

		if (canFluidFlowIntoSide(fluid, side))
		{
			onFluidFlowIntoSide(fluid, side, newFlowDecay);
			return true;
		}

		return false;
	}

	public void onFluidFlowIntoSide(Fluid fluid, ForgeDirection side, int flowDecay)
	{
		ModEarliestOfGames.Log.info("onFluidFlowIntoSide: " + fluid.getLocalizedName() + ", " + side + ", " + flowDecay);
		
		// special case: fluid without a source that is flowing down/up will have a different flow decay than fluid flowing down/up from a source
		if ((side == ForgeDirection.UP || side == ForgeDirection.DOWN) && flowDecay < FluidHelper.getQuantaPerBlock(fluid))
		{
			setInputFlow(side, null);
		}
		else
			setInputFlow(side, new FluidFlow(fluid, flowDecay));
	}

	public void onFluidFlowOutOfSide(Fluid fluid, ForgeDirection side, int flowDecay)
	{
		ModEarliestOfGames.Log.info("onFluidFlowOutOfSide: " + fluid.getLocalizedName() + ", " + side + ", " + flowDecay);
		setOutputFlow(side, new FluidFlow(fluid, flowDecay));
	}

	public int getQuantaRemaining(Fluid fluid)
	{
		int quantaRemaining = -1;
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			FluidFlow flow = getInputFlow(side);
			if (flow != null && flow.getFluid() == fluid)
			{
				if (quantaRemaining == -1)
					quantaRemaining = 0;

				quantaRemaining += getInputFlow(side).getQuantaRemaining();
			}
		}
		return Math.max(FluidHelper.getQuantaPerBlock(fluid), quantaRemaining);
	}

	@Override
	public int getFlowDecay(Fluid fluid)
	{
		int flowDecay = -1;
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			FluidFlow flow = getInputFlow(side);
			if (flow != null && flow.getFluid() == fluid)
			{
				if (flowDecay == -1)
					flowDecay = getInputFlow(side).getFlowDecay();
				else
					flowDecay = Math.min(flowDecay, getInputFlow(side).getFlowDecay());
			}
		}
		return flowDecay;
	}

	@Override
	public int getEffectiveFlowDecay(Fluid fluid)
	{
		return getFlowDecay(fluid);
	}
	
	@Override
	public int getFlowDecayTo(Fluid fluid, ForgeDirection side)
	{
		return canFluidFlowOutOfSide(fluid, side) ? getFlowDecay(fluid) : -1;
	}

	public FluidFlow getMasterFlow()
	{
		return masterFlow;
	}

	public void setMasterFlow(FluidFlow masterFlow)
	{
		FluidFlow oldFlow = getMasterFlow();
		if (!FluidFlow.areFluidFlowsEqual(oldFlow, masterFlow))
		{
			this.masterFlow = masterFlow;
			onMasterFlowChanged(oldFlow);
		}
	}

	public void setOutputFlow(ForgeDirection side, FluidFlow flow)
	{
		FluidFlow oldFlow = getOutputFlow(side);
		if (!FluidFlow.areFluidFlowsEqual(oldFlow, flow))
		{
			outputFlows[side.ordinal()] = flow;
			onOutputFlowsChanged();
		}
	}

	public void setInputFlow(ForgeDirection side, FluidFlow flow)
	{
		FluidFlow oldFlow = getInputFlow(side);
		if (!FluidFlow.areFluidFlowsEqual(oldFlow, flow))
		{
			inputFlows[side.ordinal()] = flow;
			onInputFlowsChanged();
		}
	}

	public FluidFlow getOutputFlow(ForgeDirection side)
	{
		if (side == ForgeDirection.UNKNOWN)
			return null;

		return outputFlows[side.ordinal()];
	}

	public FluidFlow getInputFlow(ForgeDirection side)
	{
		if (side == ForgeDirection.UNKNOWN)
			return null;

		return inputFlows[side.ordinal()];
	}

	public void checkInputFlowsForChanges()
	{
		ModEarliestOfGames.Log.info("checkInputFlowsForChanges");

		boolean flowChanged = false;

		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			FluidFlow flow = getInputFlow(side);
			if (flow != null)
			{
				int x = xCoord + side.offsetX, y = yCoord + side.offsetY, z = zCoord + side.offsetZ;
				Fluid fluid = FluidHelper.getFluidAt(worldObj, x, y, z, side);

				if (fluid != null)
				{
					int newFlowDecay = FluidHelper.getFlowDecayOfFluidAt(worldObj, x, y, z, flow.getFluid());
					if (newFlowDecay != flow.getFlowDecay())
					{
						flow.setFlowDecay(newFlowDecay);
						flowChanged = true;
					}

					if (fluid != flow.getFluid())
					{
						flow.setFluid(fluid);
						flowChanged = true;
					}
				}
				else
				{
					inputFlows[side.ordinal()] = null;
					flowChanged = true;
				}
			}
		}

		if (flowChanged)
		{
			onInputFlowsChanged();
		}
	}

	public List<Fluid> getInputFluidTypes()
	{
		List<Fluid> fluidTypes = new ArrayList<Fluid>();
		for (FluidFlow flow : inputFlows)
		{
			if (flow != null && !fluidTypes.contains(flow.getFluid()))
				fluidTypes.add(flow.getFluid());
		}
		return fluidTypes;
	}

	public void updateMasterFlow()
	{
		FluidFlow oldMasterFlow = masterFlow;

		List<Fluid> fluidTypes = getInputFluidTypes();
		if (fluidTypes.isEmpty())
			masterFlow = null;
		else
		{
			// TODO: better sorting here, maybe with some actual testing
			int strongestFlow = 0;
			Fluid strongestFluid = null;
			for (Fluid fluid : fluidTypes)
			{
				if (strongestFluid == null || fluid.getDensity() > strongestFluid.getDensity() || getFlowDecay(fluid) < strongestFlow)
				{
					strongestFlow = getFlowDecay(fluid);
					strongestFluid = fluid;
				}
			}
			masterFlow = new FluidFlow(strongestFluid, strongestFlow);
		}

		if (!FluidFlow.areFluidFlowsEqual(oldMasterFlow, getMasterFlow()))
		{
			onMasterFlowChanged(oldMasterFlow);
		}
	}

	public void updateOutputFlows()
	{
		updateOutputFlows(false);
	}
	
	public void updateOutputFlows(boolean forceChanged)
	{
		ModEarliestOfGames.Log.info("updateOutputFlows");
		
		boolean flowChanged = false;

		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			FluidFlow oldOutputFlow = getOutputFlow(side);

			if (getMasterFlow() != null && canFluidFlowOutOfSide(getMasterFlow().getFluid(), side))
			{
				outputFlows[side.ordinal()] = getMasterFlow();
			}
			else
			{
				outputFlows[side.ordinal()] = null;
			}

			if (!FluidFlow.areFluidFlowsEqual(oldOutputFlow, getOutputFlow(side)))
				flowChanged = true;
		}

		if (flowChanged || forceChanged)
		{
			onOutputFlowsChanged();
		}
	}
	
	public void stopOutputFlowOnSide(ForgeDirection side)
	{
		if (getOutputFlow(side) != null)
		{
			//int x = xCoord + side.offsetX, y = yCoord + side.offsetY, z = zCoord + side.offsetZ;
			outputFlows[side.ordinal()] = null;
			//worldObj.setBlockToAir(x, y, z);
		}
	}
	
	public void stopOutputFlowsOfType(Fluid fluid)
	{
		ModEarliestOfGames.Log.info("stopOutputFlowsOfType " + fluid.getLocalizedName());
		
		boolean flowChanged = false;

		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			//int x = xCoord + side.offsetX, y = yCoord + side.offsetY, z = zCoord + side.offsetZ;
			FluidFlow oldOutputFlow = getOutputFlow(side);

			if (oldOutputFlow != null && oldOutputFlow.getFluid() == fluid)
			{
				outputFlows[side.ordinal()] = null;
				//outputFluidFlowOnSide(new FluidFlow(fluid, FluidHelper.getQuantaPerBlock(fluid.getBlock())), side);
				//worldObj.setBlockToAir(x, y, z);
				flowChanged = true;
			}
		}

		if (flowChanged)
		{
			onOutputFlowsChanged();
		}
	}

	public void outputFluidFlowOnSide(FluidFlow flow, ForgeDirection side)
	{
		int x = xCoord + side.offsetX, y = yCoord + side.offsetY, z = zCoord + side.offsetZ;
		
		int newFlowDecay = FluidHelper.getNextFlowDecay(flow.getFluid(), flow.getFlowDecay(), side);
		
		if (newFlowDecay < 0)
			return;

		Hooks.onFlowIntoBlockFrom((BlockDynamicLiquid) FluidHelper.getFlowingFluidBlock(flow.getFluid()), worldObj, x, y, z, newFlowDecay, side.ordinal());
		onFluidFlowOutOfSide(flow.getFluid(), side, flow.getFlowDecay());
	}

	public void onMasterFlowChanged(FluidFlow oldFlow)
	{
		ModEarliestOfGames.Log.info("onMasterFlowChanged");

		if (oldFlow != null && getMasterFlow() == null)
			onMasterFlowStopped(oldFlow);
		else
			updateOutputFlows();

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		//worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
		
		markDirty();
	}
	
	public void onMasterFlowStopped(FluidFlow oldFlow)
	{
		ModEarliestOfGames.Log.info("onMasterFlowStopped");
		
		stopOutputFlowsOfType(oldFlow.getFluid());

		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
	}

	public void onInputFlowsChanged()
	{
		ModEarliestOfGames.Log.info("onInputFlowsChanged");

		updateMasterFlow();

		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		//worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
		
		markDirty();
	}

	public void onOutputFlowsChanged()
	{
		ModEarliestOfGames.Log.info("onOutputFlowsChanged");

		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			FluidFlow flow = getOutputFlow(side);
			if (flow != null)
			{
				outputFluidFlowOnSide(flow, side);
			}
		}
		
		markDirty();
		//worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		//worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord));
	}

	public void onNeighborBlockChange(Block block)
	{
		checkInputFlowsForChanges();
		updateOutputFlows(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
	{
		NBTTagCompound compound = pkt.func_148857_g();

		readFluidDataFromNBT(compound);
	}

	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound compound = new NBTTagCompound();

		writeFluidDataToNBT(compound);

		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, compound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		
		writeFluidDataToNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		
		readFluidDataFromNBT(compound);
	}

	public void writeFluidDataToNBT(NBTTagCompound compound)
	{
		if (compound == null)
			return;

		if (getMasterFlow() != null)
		{
			NBTTagCompound flowTag = new NBTTagCompound();
			getMasterFlow().getDescriptionPacket(flowTag);
			compound.setTag("MasterFlow", flowTag);
		}
		
		NBTTagList inputFlows = new NBTTagList();
		boolean hasInputFlows = false;

		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			FluidFlow flow = getInputFlow(side);

			if (flow != null)
			{
				NBTTagCompound flowTag = new NBTTagCompound();
				flowTag.setByte("Side", (byte) side.ordinal());
				flow.getDescriptionPacket(flowTag);
				inputFlows.appendTag(flowTag);
				hasInputFlows = true;
			}
		}

		if (hasInputFlows)
			compound.setTag("InputFlows", inputFlows);

		NBTTagList outputFlows = new NBTTagList();
		boolean hasOutputFlows = false;

		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS)
		{
			FluidFlow flow = getOutputFlow(side);

			if (flow != null)
			{
				NBTTagCompound flowTag = new NBTTagCompound();
				flowTag.setByte("Side", (byte) side.ordinal());
				flow.getDescriptionPacket(flowTag);
				outputFlows.appendTag(flowTag);
				hasOutputFlows = true;
			}
		}

		if (hasOutputFlows)
			compound.setTag("OutputFlows", outputFlows);
	}
	
	public void readFluidDataFromNBT(NBTTagCompound compound)
	{
		if (compound == null)
			return;

		FluidFlow oldMasterFlow = getMasterFlow();
		if (compound.hasKey("MasterFlow"))
		{
			NBTTagCompound flowTag = compound.getCompoundTag("MasterFlow");
			this.masterFlow = FluidFlow.getNewFluidFlowFromNBT(flowTag);
		}
		else
			this.masterFlow = null;
		
		if (this.worldObj != null && FluidFlow.areFluidFlowsEqual(oldMasterFlow, getMasterFlow()))
			onMasterFlowChanged(oldMasterFlow);
		
		// clear
		Arrays.fill(this.inputFlows, null);
		
		if (compound.hasKey("InputFlows"))
		{
			NBTTagList inputFlows = compound.getTagList("InputFlows", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < inputFlows.tagCount(); i++)
			{
				NBTTagCompound flowTag = inputFlows.getCompoundTagAt(i);
				ForgeDirection side = ForgeDirection.getOrientation(flowTag.getByte("Side"));

				if (side != ForgeDirection.UNKNOWN)
				{
					this.inputFlows[side.ordinal()] = FluidFlow.getNewFluidFlowFromNBT(flowTag);
				}
			}
		}
		if (this.worldObj != null)
			onInputFlowsChanged();

		// clear
		Arrays.fill(this.outputFlows, null);
		
		if (compound.hasKey("OutputFlows"))
		{
			NBTTagList outputFlows = compound.getTagList("OutputFlows", Constants.NBT.TAG_COMPOUND);

			for (int i = 0; i < outputFlows.tagCount(); i++)
			{
				NBTTagCompound flowTag = outputFlows.getCompoundTagAt(i);
				ForgeDirection side = ForgeDirection.getOrientation(flowTag.getByte("Side"));

				if (side != ForgeDirection.UNKNOWN)
				{
					this.outputFlows[side.ordinal()] = FluidFlow.getNewFluidFlowFromNBT(flowTag);
				}
			}
		}
		if (this.worldObj != null)
			onOutputFlowsChanged();
	}
}
