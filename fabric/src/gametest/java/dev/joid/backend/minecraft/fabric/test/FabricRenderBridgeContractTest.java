package dev.joid.backend.minecraft.fabric.test;

import dev.joid.backend.minecraft.bridge.snapshot.MinecraftSnapshotBackend;
import dev.joid.test.contract.RenderBridgeContractSuite;
import dev.joid.test.snapshot.ISnapshotBackend;
import lombok.NonNull;

public class FabricRenderBridgeContractTest extends RenderBridgeContractSuite {

	@Override
	protected @NonNull ISnapshotBackend createBackend() {
		return new MinecraftSnapshotBackend();
	}

}