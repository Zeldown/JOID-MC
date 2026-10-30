package dev.joid.backend.minecraft.demo.snapshot;

import dev.joid.base.lwjgl2.opengl.Lwjgl2GlBinding;
import dev.joid.base.opengl.snapshot.GlBorrowedTextureBackend;
import dev.joid.test.contract.BorrowedTextureContractSuite;
import dev.joid.test.contract.IBorrowedTextureBackend;
import lombok.NonNull;

public class MinecraftBorrowedTextureContractTest extends BorrowedTextureContractSuite {

	@Override
	protected @NonNull IBorrowedTextureBackend createBackend() {
		return new GlBorrowedTextureBackend(new MinecraftSnapshotBackend(), Lwjgl2GlBinding::inst);
	}

}