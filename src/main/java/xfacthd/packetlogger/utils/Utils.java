package xfacthd.packetlogger.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import xfacthd.packetlogger.PacketLogger;
import xfacthd.packetlogger.logger.data.PacketDump;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public final class Utils
{
    public static final Component VALUE_TRUE = translate("value", "true");
    public static final Component VALUE_FALSE = translate("value", "false");
    private static final String[] BYTE_UNITS = new String[] { "B", "KB", "MB", "GB" };

    public static String translateKey(String prefix, String postfix)
    {
        String content = "";
        if (prefix != null)
        {
            content += prefix + ".";
        }
        content += PacketLogger.MODID;
        if (postfix != null)
        {
            content += "." + postfix;
        }
        return content;
    }

    public static MutableComponent translate(String prefix, String postfix)
    {
        return Component.translatable(translateKey(prefix, postfix));
    }

    public static Component logBool(boolean value)
    {
        return value ? VALUE_TRUE : VALUE_FALSE;
    }

    public static String printClassName(Class<?> clazz)
    {
        String name = clazz.getName();
        int lastPeriod = name.lastIndexOf('.');
        if (lastPeriod > -1)
        {
            return name.substring(lastPeriod + 1);
        }
        return name;
    }

    public static String formatByteCount(int count)
    {
        float value = count;
        int idx = 0;
        while (value >= 1000F && idx < (BYTE_UNITS.length - 1))
        {
            value /= 1000F;
            idx++;
        }
        return "%.2f %s".formatted(value, BYTE_UNITS[idx]);
    }

    public static <T> void ifPresent(WeakReference<T> ref, Consumer<T> consumer)
    {
        T value = ref.get();
        if (value != null)
        {
            consumer.accept(value);
        }
    }

    public static MethodHandle unreflectField(Class<?> clazz, String srgFieldName)
    {
        Field field = ObfuscationReflectionHelper.findField(clazz, srgFieldName);
        try
        {
            return MethodHandles.publicLookup().unreflectGetter(field);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect field '%s#%s'".formatted(clazz.getName(), srgFieldName), e);
        }
    }

    public static void printHexDump(PacketPrinter printer, PacketDump dump)
    {
        byte[] bytes = dump.getDump();

        int lines = bytes.length / 16;
        if (bytes.length % 16 > 0)
        {
            lines++;
        }

        StringBuilder builder = new StringBuilder(
                (bytes.length * 3) + // Hex representation
                bytes.length + // Char representation
                lines * 14 + // Index prefix (10)
                lines + // Hex/char separator (1)
                lines * 2 + // Char rep end (1), new line (1)
                7 // Markdown code block markers + first new line
        );

        builder.append("```\n");
        for (int i = 0; i < lines; i++)
        {
            builder.append("%08x: ".formatted(i * 16));
            for (int j = 0; j < 16; j++)
            {
                int idx = (i * 16) + j;
                if (idx < bytes.length)
                {
                    builder.append(Integer.toString((bytes[idx] & 0xF0) >> 4, 16)) // High nibble
                            .append(Integer.toString(bytes[idx] & 0x0F, 16)) // Low nibble
                            .append(' ');
                }
                else
                {
                    builder.append("   ");
                }
            }
            builder.append('|');
            for (int j = 0; j < 16; j++)
            {
                char c = ' ';
                int idx = (i * 16) + j;
                if (idx < bytes.length)
                {
                    c = (char) bytes[idx];
                }
                if (c >= 0x20 && c <= 0x7E)
                {
                    builder.append(c);
                }
                else
                {
                    builder.append('.');
                }
            }
            builder.append("|\n");
        }
        builder.append("```");

        printer.appendRaw(builder.toString());
        printer.newLine();
    }



    private Utils() { }
}
