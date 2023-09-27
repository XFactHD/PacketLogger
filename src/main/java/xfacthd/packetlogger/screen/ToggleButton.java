package xfacthd.packetlogger.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public final class ToggleButton extends AbstractButton
{
    private static final int WIDTH = 20;

    private final BooleanConsumer stateConsumer;
    private boolean state;

    public ToggleButton(int x, int y, int height, boolean state, BooleanConsumer stateConsumer)
    {
        super(x, y, WIDTH, height, Component.empty());
        this.stateConsumer = stateConsumer;
        this.state = state;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick)
    {
        graphics.fill(getX(), getY(), getX() + width, getY() + height, isFocused() ? 0xFFFFFFFF : 0xFF000000);
        graphics.fill(getX() + 1,           getY() + 1, getX() + (width / 2), getY() + height - 1, state ? 0xFF404040 : 0xFFCC0000);
        graphics.fill(getX() + (width / 2), getY() + 1, getX() + width - 1,   getY() + height - 1, state ? 0xFF00CC00 : 0xFF404040);
    }

    @Override
    public void onPress()
    {
        state = !state;
        stateConsumer.accept(state);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput out) { }
}
