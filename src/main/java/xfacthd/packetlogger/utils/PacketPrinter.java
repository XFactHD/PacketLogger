package xfacthd.packetlogger.utils;

import com.google.common.base.Preconditions;

public final class PacketPrinter
{
    private final StringBuilder builder = new StringBuilder();
    private boolean newLine = true;
    private int indent = 0;

    public PacketPrinter append(String text)
    {
        if (newLine)
        {
            newLine = false;
            prependIndent();
        }
        boolean addNewLine = false;
        if (text.endsWith("\n"))
        {
            text = text.substring(0, text.length() - 1);
            addNewLine = true;
        }
        builder.append(text);
        if (addNewLine)
        {
            newLine();
        }
        return this;
    }

    // Only used by the hex dump, not intended for use outside of that
    void appendRaw(String text)
    {
        builder.append(text);
    }

    public PacketPrinter newLine()
    {
        return newLine(false);
    }

    public PacketPrinter newLine(boolean mdForce)
    {
        if (newLine)
        {
            prependIndent();
        }
        newLine = true;
        if (mdForce)
        {
            builder.append("  ");
        }
        builder.append("\n");
        return this;
    }

    public PacketPrinter incIndent()
    {
        indent++;
        return this;
    }

    public PacketPrinter decIndent()
    {
        indent--;
        Preconditions.checkState(indent >= 0, "Indent is underflowing");
        return this;
    }

    private void prependIndent()
    {
        if (indent > 0)
        {
            builder.append("    ".repeat(indent));
        }
    }

    public String print()
    {
        Preconditions.checkState(indent == 0, "Dangling indentation");
        return builder.toString();
    }
}
