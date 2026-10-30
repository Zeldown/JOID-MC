package dev.joid.backend.minecraft.demo.snapshot;

import dev.joid.backend.minecraft.bridge.snapshot.MinecraftSnapshotBackend;
import dev.joid.test.contract.BitmapFontContractSuite;
import dev.joid.test.snapshot.ISnapshotBackend;
import lombok.NonNull;

public class MinecraftBitmapFontContractTest extends BitmapFontContractSuite {

	@Override
	protected @NonNull ISnapshotBackend createBackend() {
		return new MinecraftSnapshotBackend();
	}

}