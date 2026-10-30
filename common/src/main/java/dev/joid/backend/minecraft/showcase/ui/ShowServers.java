package dev.joid.backend.minecraft.showcase.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.joid.backend.minecraft.lib.font.impl.minecraft.MinecraftFont;
import dev.joid.backend.minecraft.lib.ui.core.data.minecraft.UIDataMinecraft;
import dev.joid.backend.minecraft.lib.ui.node.impl.design.block.BlockNode;
import dev.joid.lib.color.Color;
import dev.joid.lib.draw.text.Text;
import dev.joid.lib.font.FontWeight;
import dev.joid.lib.font.TextInfo;
import dev.joid.lib.resource.ResourceBuilder;
import dev.joid.lib.signal.impl.primitive.StringSignal;
import dev.joid.lib.ui.core.UI;
import dev.joid.lib.ui.node.Node;
import dev.joid.lib.ui.node.effect.impl.RoundedNodeEffect;
import dev.joid.lib.ui.node.effect.impl.ShadowNodeEffect;
import dev.joid.lib.ui.node.impl.layout.ReorderableFlexNode;
import dev.joid.lib.ui.node.impl.resource.ResourceNode;
import dev.joid.lib.ui.node.impl.shape.RectNode;
import dev.joid.lib.ui.node.impl.text.TextNode;
import dev.joid.lib.ui.node.property.OverflowProperty;
import dev.joid.lib.utils.align.Align;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

@UIDataMinecraft(pause = false)
public class ShowServers extends UI implements IShowFocus {

	private static final List<Server> SERVERS = Arrays.asList(
		new Server("Emerald Valley", "\u00A7aSurvival \u00A78\u00B7 \u00A7fa new season, a fresh map", "1,204", 5, ShowTheme.GRASS, Blocks.grass, 0),
		new Server("Skyward Isles", "\u00A7bSkyblock \u00A78\u00B7 \u00A7edouble XP all weekend", "846", 5, ShowTheme.DIAMOND, Blocks.log, 0),
		new Server("Redstone Labs", "\u00A7cRedstone \u00A77& \u00A76tech \u00A78\u00B7 \u00A7fcreative plots", "312", 4, ShowTheme.REDSTONE, Blocks.redstone_block, 0),
		new Server("Nether Rush", "\u00A74PvP arena \u00A78\u00B7 \u00A76ranked season 3", "2,031", 4, ShowTheme.REDSTONE, Blocks.netherrack, 0),
		new Server("Pixel Kingdom", "\u00A7dRoleplay \u00A78\u00B7 \u00A7fbuild your own castle", "577", 3, ShowTheme.AMETHYST, Blocks.brick_block, 0),
		new Server("Frostpeak", "\u00A7bHardcore \u00A78\u00B7 \u00A7fone life, no mercy", "128", 5, ShowTheme.DIAMOND, Blocks.snow, 0),
		new Server("Deep Mines", "\u00A7eMining event \u00A78\u00B7 \u00A7bdouble ores", "693", 4, ShowTheme.GOLD, Blocks.diamond_ore, 0),
		new Server("Ocean Odyssey", "\u00A73Deep sea \u00A7fexploration \u00A78\u00B7 \u00A7anew ruins", "254", 3, ShowTheme.DIAMOND, Blocks.lapis_block, 0),
		new Server("Cherry Grove", "\u00A7dChill SMP \u00A78\u00B7 \u00A7awhitelist open", "96", 5, ShowTheme.AMETHYST, Blocks.wool, 6),
		new Server("Bed Brawl", "\u00A7cBed wars \u00A78\u00B7 \u00A7f4v4v4v4", "1,768", 2, ShowTheme.REDSTONE, Blocks.tnt, 0),
		new Server("End City", "\u00A75The End \u00A78\u00B7 \u00A7felytra races", "403", 4, ShowTheme.AMETHYST, Blocks.end_stone, 0),
		new Server("Builders Guild", "\u00A76Creative \u00A78\u00B7 \u00A7fmonthly contests", "731", 5, ShowTheme.GOLD, Blocks.crafting_table, 0)
	);

	private final Node[]       rows;
	private final List<String> order;
	private final StringSignal favorite;

	private double start;

	public ShowServers() {
		this.rows     = new Node[ShowServers.SERVERS.size()];
		this.order    = new ArrayList<>();
		this.favorite = new StringSignal(ShowServers.SERVERS.get(0).getName());
	}

	@Override
	public void init() {
		this.start = ShowTheme.now();
		this.order.clear();
		ShowServers.SERVERS.forEach(server -> this.order.add(server.getName()));

		ShowTheme
		.panel(440, 80, 1040, 920)
		.y(() -> 80D + 40D * (1D - ShowTheme.ease(this.elapsed(), 0D, 0.6D)))
		.body(panel -> {
			TextNode.create(50, 40).text(Text.create("Multiplayer", ShowTheme.font(FontWeight.EXTRA_BOLD, 44F, ShowTheme.TEXT))).attach(panel);
			TextNode.create(52, 102).text(Text.create("12 servers  \u00B7  drag to reorder, scroll for more", ShowTheme.font(FontWeight.MEDIUM, 20F, ShowTheme.MUTED))).attach(panel);
			RectNode
			.create(690, 44, 300, 56)
			.color(ShowTheme.GRASS.withAlpha(0.16F))
			.effect(RoundedNodeEffect.create(28F))
			.body(chip -> {
				TextNode.create(24, 28).text(Text.create("FAVORITE", ShowTheme.label(14F, ShowTheme.GRASS))).anchorY(Align.CENTER).attach(chip);
				TextNode.create(276, 28).text(Text.create(this.favorite::get, ShowTheme.font(FontWeight.BOLD, 20F, ShowTheme.TEXT)).alignX(Align.END)).anchorX(Align.END).anchorY(Align.CENTER).attach(chip);
			})
			.attach(panel);
			RectNode
			.create(30, 160, 980, 730)
			.color(Color.TRANSPARENT)
			.overflow(OverflowProperty.SCROLL)
			.scrollbar(ShowScrollbarNode.create(964, 0, 8, 730))
			.body(area -> {
				final ReorderableFlexNode list = ReorderableFlexNode
				.vertical(20, 0, 920)
				.onReorderEnd(event -> {
					this.order.add(event.getIndex(), this.order.remove(event.getPrevious()));
					this.favorite.set(this.order.get(0));
				})
				.margin(12D);
				list.body(flex -> {
					for (int index = 0; index < ShowServers.SERVERS.size(); index++) {
						this.row(list, index);
					}
				});
				list.attach(area);
			})
			.attach(panel);
		})
		.attach(this);
	}

	@Override
	public Node getFocus() {
		return this.rows[3];
	}

	public Node getRow(final int index) {
		return this.rows[index];
	}

	private void row(final ReorderableFlexNode list, final int index) {
		final Server server = ShowServers.SERVERS.get(index);
		this.rows[index] = RectNode
		.create(0, 0, 920, 88)
		.<RectNode>self(row -> row.color(() -> list.isDragging(row) ? Color.decode("#253046") : Color.WHITE.withAlpha(0.05F + row.hoverValue(0.05F))))
		.<RectNode>self(row -> row.effect(ShadowNodeEffect.create(server.getColor(), 30F).color(() -> server.getColor().withAlpha(list.isDragging(row) ? 0.5F : 0F))))
		.effect(RoundedNodeEffect.create(20F))
		.body(row -> {
			ShowTheme
			.tile(16, 12, 64, 64, 14F)
			.body(icon -> {
				BlockNode.create(10, 10, 44, 44).block(server.getBlock()).metadata(server.getMetadata()).rotationYaw(() -> 30D + this.elapsed() * 25D + index * 30D).interactive(false).attach(icon);
			})
			.attach(row);
			TextNode.create(102, 28).text(Text.create(server.getName(), ShowTheme.font(FontWeight.SEMI_BOLD, 24F, ShowTheme.TEXT))).anchorY(Align.CENTER).attach(row);
			TextNode.create(102, 54).text(Text.create(server.getMotd(), TextInfo.create(MinecraftFont.DEFAULT, 16F).color(ShowTheme.TEXT).shadow(2F, 2F))).attach(row);
			TextNode.create(820, 44).text(Text.create(server.getPlayers(), ShowTheme.font(FontWeight.SEMI_BOLD, 20F, ShowTheme.MUTED)).alignX(Align.END)).anchorX(Align.END).anchorY(Align.CENTER).attach(row);
			ResourceNode.create(850, 36, 30, 24).resource(ResourceBuilder.create().nearest().textureCoords(0D, (176D + (5 - server.getPing()) * 8D) / 256D, 10D / 256D, (184D + (5 - server.getPing()) * 8D) / 256D).of("minecraft:textures/gui/icons.png")).interactive(false).attach(row);
		})
		.hoverDuration(160L)
		.attach(list);
	}

	private double elapsed() {
		return ShowTheme.now() - this.start;
	}

	@Getter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	private static final class Server {

		private final String name;
		private final String motd;
		private final String players;
		private final int    ping;
		private final Color  color;
		private final Block  block;
		private final int    metadata;

	}

}