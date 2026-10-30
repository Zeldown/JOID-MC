package dev.joid.backend.minecraft.demo.snapshot;

import dev.joid.test.contract.RenderBridgeContractSuite;
import dev.joid.test.snapshot.ISnapshotBackend;
import lombok.NonNull;

public class MinecraftRenderBridgeContractTest extends RenderBridgeContractSuite {

	@Override
	protected @NonNull ISnapshotBackend createBackend() {
		return new MinecraftSnapshotBackend();
	}

}