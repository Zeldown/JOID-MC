package dev.joid.backend.minecraft.demo.snapshot;

import dev.joid.test.contract.BorrowedTextureContractSuite;
import dev.joid.test.contract.IBorrowedTextureBackend;
import lombok.NonNull;

public class MinecraftBorrowedTextureContractTest extends BorrowedTextureContractSuite {

	@Override
	protected @NonNull IBorrowedTextureBackend createBackend() {
		return new MinecraftBorrowedTextureBackend();
	}

}