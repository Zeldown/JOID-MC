package dev.joid.backend.minecraft.demo.snapshot;

import dev.joid.backend.minecraft.bridge.snapshot.MinecraftSnapshotBackend;
import dev.joid.base.lwjgl3.opengl.snapshot.Lwjgl3StateGuardBackend;
import dev.joid.test.contract.IStateGuardBackend;
import dev.joid.test.contract.StateGuardContractSuite;
import lombok.NonNull;

public class MinecraftStateGuardContractTest extends StateGuardContractSuite {

	@Override
	protected @NonNull IStateGuardBackend createBackend() {
		return new Lwjgl3StateGuardBackend(new MinecraftSnapshotBackend());
	}

}