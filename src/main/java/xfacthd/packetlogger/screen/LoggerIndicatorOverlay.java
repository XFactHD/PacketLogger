package xfacthd.packetlogger.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import xfacthd.packetlogger.logger.PacketLogHandler;

public final class LoggerIndicatorOverlay implements IGuiOverlay
{
    private static final Component MSG_TITLE = Component.literal("PacketLogger");
    public static final Component MSG_IDLE = Component.translatable("msg.packetlogger.state.idle");
    public static final Component MSG_RECORDING = Component.translatable("msg.packetlogger.state.recording");
    private static final int X_OFFSET = 10;
    private static final int Y_OFFSET = 10;
    private static final int HEIGHT = 19;
    private static final int ICON_SIZE = 15;
    private static final int PADDING = 5;
    private static final int LINE_HEIGHT = 10;
    private static final int U_OFF_REC = 192;
    private static final int U_OFF_IDLE = U_OFF_REC + 32;

    @Override
    public void render(ForgeGui gui, GuiGraphics graphics, float partialTick, int screenWidth, int screenHeight)
    {
        boolean active = PacketLogHandler.isActive();
        Component msg = active ? MSG_RECORDING : MSG_IDLE;

        Font font = gui.getFont();
        int width = ICON_SIZE + PADDING + Math.max(font.width(MSG_TITLE), Math.max(font.width(MSG_RECORDING), font.width(MSG_IDLE)));
        int x = screenWidth - width - X_OFFSET;
        int y = Y_OFFSET;

        TooltipRenderUtil.renderTooltipBackground(graphics, x, y, width, HEIGHT, 0);

        int uOff = active ? U_OFF_REC : U_OFF_IDLE;
        graphics.blit(AbstractWidget.WIDGETS_LOCATION, x, y + 2, uOff, 0, ICON_SIZE, ICON_SIZE);
        graphics.drawString(gui.getFont(), MSG_TITLE, x + ICON_SIZE + PADDING, y + 1, 0xE0E0E0, false);
        graphics.drawString(gui.getFont(), msg, x + ICON_SIZE + PADDING, y + 1 + LINE_HEIGHT, 0xE0E0E0, false);
    }
}
