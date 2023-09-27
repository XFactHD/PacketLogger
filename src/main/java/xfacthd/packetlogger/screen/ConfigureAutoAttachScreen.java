package xfacthd.packetlogger.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import xfacthd.packetlogger.event.ClientEvents;
import xfacthd.packetlogger.logger.PacketLogContext;
import xfacthd.packetlogger.logger.PacketLogHandler;
import xfacthd.packetlogger.utils.ClientDelegatingCommandSource;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;

public final class ConfigureAutoAttachScreen extends Screen
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation("textures/gui/demo_background.png");
    private static final String[] EMPTY_FILTERS = new String[0];
    private static final int LINE_HEIGHT = 12;
    private static final int PADDING = 5;
    private static final int FILTER_HEIGHT = 20;
    private static final int BUTTON_HEIGHT = 20;
    private static final int TOGGLE_BTN_HEIGHT = LINE_HEIGHT - 2;
    public static final Component TITLE = Component.translatable("title.packetlogger.cfg_auto_attach");
    private static final Component BTN_TITLE = Component.literal("PacketLogger");
    public static final Component BTN_TOOLTIP_DISARMED = Component.translatable("tooltip.packetlogger.btn.auto_attach.disarmed");
    public static final Component BTN_TOOLTIP_ARMED = Component.translatable("tooltip.packetlogger.btn.auto_attach.armed");
    public static final Component[] LINES = new Component[] {
            Component.translatable("text.packetlogger.cfg_auto_attach.inbound"),
            Component.translatable("text.packetlogger.cfg_auto_attach.outbound"),
            Component.translatable("text.packetlogger.cfg_auto_attach.detailed"),
            Component.translatable("text.packetlogger.cfg_auto_attach.size"),
            Component.translatable("text.packetlogger.cfg_auto_attach.dump")
    };
    public static final Component LINE_FILTERS = Component.translatable("text.packetlogger.cfg_auto_attach.filter");
    public static final String MSG_INVALID_FILTERS = "msg.packetlogger.cfg_auto_attach.invalid_filters";
    public static final Component BTN_TOOLTIP_NO_CAPTURE = Component.translatable("tooltip.packetlogger.btn.confirm.no_capture");

    private int xLeft;
    private int yTop;
    private int imageWidth;
    private int imageHeight;
    private EditBox filterBox;
    private Button btnConfirm;
    private boolean captureValid = true;

    private boolean inbound = PacketLogHandler.INBOUND_DEFAULT;
    private boolean outbound = PacketLogHandler.OUTBOUND_DEFAULT;
    private boolean fullLog = PacketLogHandler.FULL_LOG_DEFAULT;
    private boolean logSize = PacketLogHandler.LOG_SIZE_DEFAULT;
    private boolean hexDump = PacketLogHandler.HEXDUMP_DEFAULT;
    private String filtersFromPrevCtx = null;

    public ConfigureAutoAttachScreen(PacketLogContext ctx)
    {
        super(TITLE);
        if (ctx != null)
        {
            inbound = ctx.inbound();
            outbound = ctx.outbound();
            fullLog = ctx.fullLog();
            logSize = ctx.logSize();
            hexDump = ctx.hexDump();
            filtersFromPrevCtx = String.join(", ", ctx.filters());
        }
    }

    @Override
    protected void init()
    {
        super.init();

        int maxLineWidth = Arrays.stream(LINES).mapToInt(font::width).max().orElseThrow();
        imageWidth = Math.max(maxLineWidth + PADDING * 3, Math.max(font.width(title), font.width(LINE_FILTERS)) + PADDING * 2);
        imageHeight = (PADDING * 5) + (LINES.length * LINE_HEIGHT) + LINE_HEIGHT + font.lineHeight + FILTER_HEIGHT + BUTTON_HEIGHT;
        xLeft = (width / 2) - (imageWidth / 2);
        yTop = (height / 2) - (imageHeight / 2);

        int checkX = xLeft + (PADDING * 2) + maxLineWidth;
        int checkY = yTop + PADDING + (LINE_HEIGHT * 2);
        addRenderableWidget(new ToggleButton(checkX, checkY - 1, TOGGLE_BTN_HEIGHT, inbound , state ->
        {
            inbound = state;
            checkCaptureValid();
        }));
        checkY += LINE_HEIGHT;
        addRenderableWidget(new ToggleButton(checkX, checkY - 1, TOGGLE_BTN_HEIGHT, outbound, state ->
        {
            outbound = state;
            checkCaptureValid();
        }));
        checkY += LINE_HEIGHT;
        addRenderableWidget(new ToggleButton(checkX, checkY - 1, TOGGLE_BTN_HEIGHT, fullLog , state -> fullLog  = state));
        checkY += LINE_HEIGHT;
        addRenderableWidget(new ToggleButton(checkX, checkY - 1, TOGGLE_BTN_HEIGHT, logSize , state -> logSize  = state));
        checkY += LINE_HEIGHT;
        addRenderableWidget(new ToggleButton(checkX, checkY - 1, TOGGLE_BTN_HEIGHT, hexDump , state -> hexDump  = state));

        int filterX = xLeft + PADDING + 1;
        int filterY = yTop + PADDING + (LINE_HEIGHT * 2) + (LINE_HEIGHT * LINES.length) + LINE_HEIGHT - 1;
        int filterWidth = imageWidth - (PADDING * 2) - 2;
        filterBox = addRenderableWidget(new EditBox(font, filterX, filterY, filterWidth, FILTER_HEIGHT - 2, filterBox, Component.empty()));
        if (filtersFromPrevCtx != null)
        {
            filterBox.setValue(filtersFromPrevCtx);
            filtersFromPrevCtx = null;
        }

        int btnWidth = (imageWidth - (PADDING * 3)) / 2;
        btnConfirm = addRenderableWidget(Button.builder(CommonComponents.GUI_OK, this::confirmSettings)
                .pos(xLeft + PADDING, yTop + imageHeight - PADDING - BUTTON_HEIGHT)
                .size(btnWidth, BUTTON_HEIGHT)
                .build()
        );
        btnConfirm.active = captureValid;
        if (!captureValid)
        {
            btnConfirm.setTooltip(Tooltip.create(BTN_TOOLTIP_NO_CAPTURE));
        }
        addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, btn -> onClose())
                .pos(xLeft + imageWidth - PADDING - btnWidth, yTop + imageHeight - PADDING - BUTTON_HEIGHT)
                .size(btnWidth, BUTTON_HEIGHT)
                .build()
        );
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks)
    {
        graphics.fillGradient(0, 0, width, height, 0xC0101010, 0xD0101010);
        graphics.blitNineSliced(BACKGROUND, xLeft, yTop, imageWidth, imageHeight, 4, 4, 248, 166, 0, 0);

        int x = xLeft + PADDING;
        int y = yTop + PADDING;
        graphics.drawString(font, title, xLeft + PADDING, yTop + PADDING, 0x404040, false);
        y += LINE_HEIGHT * 2;
        for (Component line : LINES)
        {
            graphics.drawString(font, line, x, y, 0x404040, false);
            y += LINE_HEIGHT;
        }
        graphics.drawString(font, LINE_FILTERS, x, y, 0x404040, false);

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        if (!super.mouseClicked(pMouseX, pMouseY, pButton))
        {
            setFocused(null);
            return false;
        }
        if (filterBox.isFocused())
        {
            filterBox.setTooltip(null);
            filterBox.setTextColor(0xE0E0E0);
        }
        return true;
    }

    private void checkCaptureValid()
    {
        boolean valid = inbound || outbound;
        if (valid != captureValid)
        {
            captureValid = valid;
            btnConfirm.active = valid;
            btnConfirm.setTooltip(valid ? null : Tooltip.create(BTN_TOOLTIP_NO_CAPTURE));
        }
    }

    private void confirmSettings(Button btn)
    {
        String[] filters = EMPTY_FILTERS;
        if (!filterBox.getValue().isEmpty())
        {
            filters = Arrays.stream(filterBox.getValue().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toArray(String[]::new);
        }
        Set<String> brokenFilters = PacketLogHandler.validatePacketFilters(filters);
        if (!brokenFilters.isEmpty())
        {
            String filterList = String.join(", ", brokenFilters);
            filterBox.setTooltip(Tooltip.create(Component.translatable(MSG_INVALID_FILTERS, filterList)));
            filterBox.setTextColor(0xFF0000);
            return;
        }
        ClientEvents.armAutoAttach(new PacketLogContext(
                new WeakReference<>(ClientDelegatingCommandSource.INSTANCE), inbound, outbound, fullLog, logSize, hexDump, -1, filters
        ));

        onClose();
    }



    public static void injectButton(Screen screen, Consumer<GuiEventListener> widgetAdder)
    {
        Button btn = new IndicatorButton(
                screen.width - 100 - 5,
                5,
                100,
                20,
                BTN_TITLE,
                ClientEvents::isAutoAttachArmed,
                checked -> checked ? BTN_TOOLTIP_ARMED : BTN_TOOLTIP_DISARMED,
                ConfigureAutoAttachScreen::handleAutoAttachButton
        );
        widgetAdder.accept(btn);
    }

    private static void handleAutoAttachButton(@SuppressWarnings("unused") Button btn)
    {
        PacketLogContext autoAttachCtx = ClientEvents.getAutoAttachContext();
        Minecraft.getInstance().pushGuiLayer(new ConfigureAutoAttachScreen(autoAttachCtx));
    }
}
