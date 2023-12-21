package xfacthd.packetlogger.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
import net.neoforged.neoforge.client.gui.overlay.IGuiOverlay;
import xfacthd.packetlogger.logger.PacketLogHandler;
import xfacthd.packetlogger.utils.Utils;

public final class LoggerIndicatorOverlay implements IGuiOverlay
{
    private static final ResourceLocation RECORDIN_IDLE_TEXTURE = Utils.rl("recording_idle");
    private static final ResourceLocation RECORDIN_ACTIVE_TEXTURE = Utils.rl("recording_active");
    private static final Component MSG_TITLE = Component.literal("PacketLogger");
    public static final Component MSG_IDLE = Component.translatable("msg.packetlogger.state.idle");
    public static final Component MSG_RECORDING = Component.translatable("msg.packetlogger.state.recording");
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 10;
    private static final int HEIGHT = 19;
    private static final int ICON_SIZE = 15;
    private static final int PADDING = 5;
    private static final int LINE_HEIGHT = 10;

    @Override
    public void render(ExtendedGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight)
    {
        boolean active = PacketLogHandler.isActive();
        Component msg = active ? MSG_RECORDING : MSG_IDLE;

        Font font = gui.getFont();
        int width = ICON_SIZE + PADDING + Math.max(font.width(MSG_TITLE), Math.max(font.width(MSG_RECORDING), font.width(MSG_IDLE)));
        int x = screenWidth - width - X_OFFSET;
        int y = Y_OFFSET;

        TooltipRenderUtil.renderTooltipBackground(graphics, x, y, width, HEIGHT, 0);

        ResourceLocation tex = active ? RECORDIN_ACTIVE_TEXTURE : RECORDIN_IDLE_TEXTURE;
        graphics.blitSprite(tex, x, y, 0, ICON_SIZE, ICON_SIZE);
        graphics.drawString(gui.getFont(), MSG_TITLE, x + ICON_SIZE + PADDING, y + 1, 0xE0E0E0, false);
        graphics.drawString(gui.getFont(), msg, x + ICON_SIZE + PADDING, y + 1 + LINE_HEIGHT, 0xE0E0E0, false);
    }
}
