package dev.joid.backend.minecraft.lib.ui.node.impl.design.block;

import java.util.function.Supplier;

import dev.joid.backend.minecraft.lib.draw.block.DrawBlock;
import dev.joid.lib.signal.Signal;
import dev.joid.lib.ui.node.Node;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.block.Block;

@Getter
public class BlockNode extends Node {

	private Block block;
	private int   metadata;

	private double rotationYaw;
	private double rotationPitch;

	protected BlockNode(final double x, final double y, final double width, final double height) {
		super(x, y, width, height);

		this.rotationYaw   = 225D;
		this.rotationPitch = 30D;

		super.<BlockNode>require("block", "call block(...) before attaching it", node -> node.block != null);
	}

	public static @NonNull BlockNode create(final double x, final double y, final double width, final double height) {
		return new BlockNode(x, y, width, height);
	}

	@Override
	public void draw(final double mouseX, final double mouseY) {
		final double size = Math.min(super.getWidth(), super.getHeight());
		DrawBlock.inst().drawBlock(super.getX() + (super.getWidth() - size) / 2D, super.getY() + (super.getHeight() - size) / 2D, size, this.block, this.metadata, this.rotationYaw, this.rotationPitch);
	}

	public final <T extends BlockNode> @NonNull T block(final @NonNull Block block) {
		return this.block(Signal.from(block));
	}

	public final <T extends BlockNode> @NonNull T block(final @NonNull Supplier<@NonNull Block> block) {
		return super.follow("block", block, value -> this.block = value);
	}

	public final <T extends BlockNode> @NonNull T metadata(final int metadata) {
		return this.metadata(Signal.from(metadata));
	}

	public final <T extends BlockNode> @NonNull T metadata(final @NonNull Supplier<Integer> metadata) {
		return super.follow("metadata", metadata, value -> this.metadata = value);
	}

	public final <T extends BlockNode> @NonNull T rotationYaw(final double rotationYaw) {
		return this.rotationYaw(Signal.from(rotationYaw));
	}

	public final <T extends BlockNode> @NonNull T rotationYaw(final @NonNull Supplier<Double> rotationYaw) {
		return super.follow("rotationYaw", rotationYaw, value -> this.rotationYaw = value);
	}

	public final <T extends BlockNode> @NonNull T rotationPitch(final double rotationPitch) {
		return this.rotationPitch(Signal.from(rotationPitch));
	}

	public final <T extends BlockNode> @NonNull T rotationPitch(final @NonNull Supplier<Double> rotationPitch) {
		return super.follow("rotationPitch", rotationPitch, value -> this.rotationPitch = value);
	}

}