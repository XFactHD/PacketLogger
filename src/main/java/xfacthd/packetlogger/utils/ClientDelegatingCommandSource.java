package xfacthd.packetlogger.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import xfacthd.packetlogger.PacketLogger;

import java.util.Optional;

public final class ClientDelegatingCommandSource implements CommandSource
{
    public static final CommandSource INSTANCE = new ClientDelegatingCommandSource();

    private ClientDelegatingCommandSource() { }

    @Override
    public void sendSystemMessage(Component msg)
    {
        player().ifPresentOrElse(
                player -> player.sendSystemMessage(msg),
                () -> PacketLogger.LOGGER.info(msg.getString())
        );
    }

    @Override
    public boolean acceptsSuccess()
    {
        return player().map(Player::acceptsSuccess).orElse(true);
    }

    @Override
    public boolean acceptsFailure()
    {
        return player().map(Player::acceptsFailure).orElse(true);
    }

    @Override
    public boolean shouldInformAdmins()
    {
        return player().map(Player::shouldInformAdmins).orElse(false);
    }

    @Override
    public boolean alwaysAccepts()
    {
        return player().map(Player::alwaysAccepts).orElse(false);
    }

    private static Optional<Player> player()
    {
        return Optional.ofNullable(Minecraft.getInstance().player);
    }
}
