package dev.joid.backend.minecraft.loader.network;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class PayloadTypeTest {

	@Test
	public void givesThePayloadAndThePlayerToTheServerCallback() {
		final List<String> received = new ArrayList<>();
		final TestPayload payload = new TestPayload();
		payload.getType().onServer((value, player) -> received.add(player)).fireServer(payload, "player");
		Assert.assertEquals(1, received.size());
		Assert.assertEquals("player", received.get(0));
	}

	@Test
	public void refusesToSendWithoutSender() {
		try {
			new TestPayload().sendToServer();
			Assert.fail("A payload was sent without sender");
		} catch (final IllegalStateException e) {
			Assert.assertEquals("No payload sender registered, the payload joid:test can only be sent from a client", e.getMessage());
		}
	}

	private static final class TestPayload implements IPayload {

		private static final PayloadType<TestPayload, String> TYPE = PayloadType.create("joid:test", reader -> new TestPayload());

		@Override
		public PayloadType<TestPayload, String> getType() {
			return TestPayload.TYPE;
		}

		@Override
		public void write(final IPayloadWriter writer) {}

	}

}