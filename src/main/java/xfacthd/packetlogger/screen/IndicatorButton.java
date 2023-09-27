package xfacthd.packetlogger.screen;

import it.unimi.dsi.fastutil.booleans.Boolean2ObjectFunction;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import xfacthd.packetlogger.PacketLogger;

import java.util.function.BooleanSupplier;

public final class IndicatorButton extends Button
{
    private static final ResourceLocation INDICATOR_TEXTURE = new ResourceLocation(PacketLogger.MODID, "textures/gui/indicator.png");
    private static final int TEXTURE_WIDTH = 16;
    private static final int TEXTURE_HEIGHT = 32;
    private static final int INDICATOR_SIZE = 13;

    private final BooleanSupplier checkedSupplier;
    private final Boolean2ObjectFunction<Component> tooltipSupplier;
    private boolean lastChecked = false;

    public IndicatorButton(int x, int y, int w, int h, Component text, BooleanSupplier checkedSupplier, Boolean2ObjectFunction<Component> tooltipSupplier, OnPress onPress)
    {
        super(x, y, w, h, text, onPress, Button.DEFAULT_NARRATION);
        this.checkedSupplier = checkedSupplier;
        this.tooltipSupplier = tooltipSupplier;
        this.lastChecked = checkedSupplier.getAsBoolean();
        setTooltip(Tooltip.create(tooltipSupplier.get(lastChecked)));
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        super.renderWidget(graphics, mouseX, mouseY, partialTick);

        boolean checked = checkedSupplier.getAsBoolean();
        if (checked != lastChecked)
        {
            lastChecked = checked;
            setTooltip(Tooltip.create(tooltipSupplier.get(lastChecked)));
        }

        int x = getX() + width - INDICATOR_SIZE - 3;
        int y = getY() + 3;
        int texY = checked ? INDICATOR_SIZE : 0;
        renderTexture(graphics, INDICATOR_TEXTURE, x, y, 0, texY, 0, INDICATOR_SIZE, INDICATOR_SIZE, TEXTURE_WIDTH, TEXTURE_HEIGHT);
    }

    @Override
    public void renderString(GuiGraphics graphics, Font font, int color)
    {
        int minX = getX() + 2;
        int maxX = getX() + getWidth() - INDICATOR_SIZE - 6;
        renderScrollingString(graphics, font, getMessage(), minX, getY(), maxX, getY() + getHeight(), color);
    }
}
