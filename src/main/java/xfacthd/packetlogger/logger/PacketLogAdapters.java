package xfacthd.packetlogger.logger;

import com.google.common.collect.Sets;
import net.minecraft.network.protocol.common.*;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.login.*;
import net.neoforged.fml.loading.FMLEnvironment;
import xfacthd.packetlogger.PacketLogger;
import xfacthd.packetlogger.logger.adapters.*;

import java.util.*;

public final class PacketLogAdapters
{
    private static Set<String> knownPackets = new HashSet<>(PacketLogHandler.getPacketTypeNames().size());
    private static int specialCount = 0;

    public static void init()
    {
        // Global
        packet("BundleDelimiterPacket", null);

        // Status
        packet("ClientboundPongResponsePacket", null);
        packet("ClientboundStatusResponsePacket", null);
        packet("ServerboundPingRequestPacket", null);
        packet("ServerboundStatusRequestPacket", null);

        // Handshake
        packet("ClientIntentionPacket", null);

        // Login
        packet("ClientboundCustomQueryPacket", PacketLogConverter.registerAdapter(ClientboundCustomQueryPacket.class, new CustomQueryPacketLogAdapter.Clientbound()));
        packet("ClientboundGameProfilePacket", null);
        packet("ClientboundHelloPacket", PacketLogConverter.registerAdapter(ClientboundHelloPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ClientboundLoginCompressionPacket", null);
        packet("ClientboundLoginDisconnectPacket", null);
        packet("ServerboundCustomQueryAnswerPacket", PacketLogConverter.registerAdapter(ServerboundCustomQueryAnswerPacket.class, new CustomQueryPacketLogAdapter.Serverbound()));
        packet("ServerboundHelloPacket", null);
        packet("ServerboundKeyPacket", PacketLogConverter.registerAdapter(ServerboundKeyPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ServerboundLoginAcknowledgedPacket", null);

        // Common
        packet("ClientboundCustomPayloadPacket", PacketLogConverter.registerAdapter(ClientboundCustomPayloadPacket.class, new CustomPayloadPacketLogAdapter.Clientbound()));
        packet("ClientboundDisconnectPacket", null);
        packet("ClientboundKeepAlivePacket", null);
        packet("ClientboundPingPacket", null);
        packet("ClientboundResourcePackPopPacket", null);
        packet("ClientboundResourcePackPushPacket", null);
        packet("ClientboundUpdateTagsPacket", PacketLogConverter.registerAdapter(ClientboundUpdateTagsPacket.class, new ClientboundUpdateDatapackDataPacketLogAdapter.Tags()));
        packet("ServerboundClientInformationPacket", null);
        packet("ServerboundCustomPayloadPacket", PacketLogConverter.registerAdapter(ServerboundCustomPayloadPacket.class, new CustomPayloadPacketLogAdapter.Serverbound()));
        packet("ServerboundKeepAlivePacket", null);
        packet("ServerboundPongPacket", null);
        packet("ServerboundResourcePackPacket", null);

        // Configuration
        packet("ClientboundFinishConfigurationPacket", null);
        packet("ClientboundRegistryDataPacket", null);
        packet("ClientboundUpdateEnabledFeaturesPacket", null);
        packet("ServerboundFinishConfigurationPacket", null);

        // Play
        packet("ClientboundAddEntityPacket", PacketLogConverter.registerAdapter(ClientboundAddEntityPacket.class, new ClientboundAddEntityPacketLogAdapter()));
        packet("ClientboundAddExperienceOrbPacket", null);
        packet("ClientboundAnimatePacket", null);
        packet("ClientboundAwardStatsPacket", null);
        packet("ClientboundBlockChangedAckPacket", null);
        packet("ClientboundBlockDestructionPacket", null);
        packet("ClientboundBlockEntityDataPacket", PacketLogConverter.registerAdapter(ClientboundBlockEntityDataPacket.class, new ClientboundBlockEntityDataPacketLogAdapter()));
        packet("ClientboundBlockEventPacket", null);
        packet("ClientboundBlockUpdatePacket", null);
        packet("ClientboundBossEventPacket", null);
        packet("ClientboundBundlePacket", PacketLogConverter.registerAdapter(ClientboundBundlePacket.class, new BundlePacketLogAdapter()));
        packet("ClientboundChangeDifficultyPacket", null);
        packet("ClientboundChunkBatchFinishedPacket", null);
        packet("ClientboundChunkBatchStartPacket", null);
        packet("ClientboundChunksBiomesPacket", null);
        packet("ClientboundClearTitlesPacket", null);
        packet("ClientboundCommandsPacket", null);
        packet("ClientboundCommandSuggestionsPacket", null);
        packet("ClientboundContainerClosePacket", null);
        packet("ClientboundContainerSetContentPacket", null);
        packet("ClientboundContainerSetDataPacket", null);
        packet("ClientboundContainerSetSlotPacket", null);
        packet("ClientboundCooldownPacket", null);
        packet("ClientboundCustomChatCompletionsPacket", null);
        packet("ClientboundDamageEventPacket", null);
        packet("ClientboundDeleteChatPacket", PacketLogConverter.registerAdapter(ClientboundDeleteChatPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ClientboundDisguisedChatPacket", null);
        packet("ClientboundEntityEventPacket", null);
        packet("ClientboundExplodePacket", null);
        packet("ClientboundForgetLevelChunkPacket", null);
        packet("ClientboundGameEventPacket", null);
        packet("ClientboundHorseScreenOpenPacket", null);
        packet("ClientboundHurtAnimationPacket", null);
        packet("ClientboundInitializeBorderPacket", null);
        packet("ClientboundLevelChunkWithLightPacket", PacketLogConverter.registerAdapter(ClientboundLevelChunkWithLightPacket.class, new ClientboundLevelChunkWithLightPacketLogAdapter()));
        packet("ClientboundLevelEventPacket", null);
        packet("ClientboundLevelParticlesPacket", null);
        packet("ClientboundLightUpdatePacket", null);
        packet("ClientboundLoginPacket", null);
        packet("ClientboundMapItemDataPacket", null);
        packet("ClientboundMerchantOffersPacket", null);
        packet("ClientboundMoveEntityPacket$Pos", null);
        packet("ClientboundMoveEntityPacket$PosRot", null);
        packet("ClientboundMoveEntityPacket$Rot", null);
        packet("ClientboundMoveVehiclePacket", null);
        packet("ClientboundOpenBookPacket", null);
        packet("ClientboundOpenScreenPacket", null);
        packet("ClientboundOpenSignEditorPacket", null);
        packet("ClientboundPlaceGhostRecipePacket", null);
        packet("ClientboundPlayerAbilitiesPacket", null);
        packet("ClientboundPlayerChatPacket", PacketLogConverter.registerAdapter(ClientboundPlayerChatPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ClientboundPlayerCombatEndPacket", null);
        packet("ClientboundPlayerCombatEnterPacket", null);
        packet("ClientboundPlayerCombatKillPacket", null);
        packet("ClientboundPlayerInfoRemovePacket", null);
        packet("ClientboundPlayerInfoUpdatePacket", PacketLogConverter.registerAdapter(ClientboundPlayerInfoUpdatePacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ClientboundPlayerLookAtPacket", null);
        packet("ClientboundPlayerPositionPacket", null);
        packet("ClientboundRecipePacket", null);
        packet("ClientboundRemoveEntitiesPacket", null);
        packet("ClientboundRemoveMobEffectPacket", null);
        packet("ClientboundResetScorePacket", null);
        packet("ClientboundRespawnPacket", null);
        packet("ClientboundRotateHeadPacket", null);
        packet("ClientboundSectionBlocksUpdatePacket", null);
        packet("ClientboundSelectAdvancementsTabPacket", null);
        packet("ClientboundServerDataPacket", null);
        packet("ClientboundSetActionBarTextPacket", null);
        packet("ClientboundSetBorderCenterPacket", null);
        packet("ClientboundSetBorderLerpSizePacket", null);
        packet("ClientboundSetBorderSizePacket", null);
        packet("ClientboundSetBorderWarningDelayPacket", null);
        packet("ClientboundSetBorderWarningDistancePacket", null);
        packet("ClientboundSetCameraPacket", null);
        packet("ClientboundSetCarriedItemPacket", null);
        packet("ClientboundSetChunkCacheCenterPacket", null);
        packet("ClientboundSetChunkCacheRadiusPacket", null);
        packet("ClientboundSetDefaultSpawnPositionPacket", null);
        packet("ClientboundSetDisplayObjectivePacket", null);
        packet("ClientboundSetEntityDataPacket", null);
        packet("ClientboundSetEntityLinkPacket", null);
        packet("ClientboundSetEntityMotionPacket", null);
        packet("ClientboundSetEquipmentPacket", null);
        packet("ClientboundSetExperiencePacket", null);
        packet("ClientboundSetHealthPacket", null);
        packet("ClientboundSetObjectivePacket", null);
        packet("ClientboundSetPassengersPacket", null);
        packet("ClientboundSetPlayerTeamPacket", null);
        packet("ClientboundSetScorePacket", null);
        packet("ClientboundSetSimulationDistancePacket", null);
        packet("ClientboundSetSubtitleTextPacket", null);
        packet("ClientboundSetTimePacket", null);
        packet("ClientboundSetTitlesAnimationPacket", null);
        packet("ClientboundSetTitleTextPacket", null);
        packet("ClientboundSoundEntityPacket", null);
        packet("ClientboundSoundPacket", null);
        packet("ClientboundStartConfigurationPacket", null);
        packet("ClientboundStopSoundPacket", null);
        packet("ClientboundSystemChatPacket", PacketLogConverter.registerAdapter(ServerboundChatCommandPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ClientboundTabListPacket", null);
        packet("ClientboundTagQueryPacket", null);
        packet("ClientboundTakeItemEntityPacket", null);
        packet("ClientboundTeleportEntityPacket", null);
        packet("ClientboundTickingStatePacket", null);
        packet("ClientboundTickingStepPacket", null);
        packet("ClientboundUpdateAdvancementsPacket", null);
        packet("ClientboundUpdateAttributesPacket", null);
        packet("ClientboundUpdateMobEffectPacket", null);
        packet("ClientboundUpdateRecipesPacket", PacketLogConverter.registerAdapter(ClientboundUpdateRecipesPacket.class, new ClientboundUpdateDatapackDataPacketLogAdapter.Recipes()));
        packet("ServerboundAcceptTeleportationPacket", null);
        packet("ServerboundBlockEntityTagQuery", null);
        packet("ServerboundChangeDifficultyPacket", null);
        packet("ServerboundChatAckPacket", null);
        packet("ServerboundChatCommandPacket", PacketLogConverter.registerAdapter(ServerboundChatCommandPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ServerboundChatPacket", PacketLogConverter.registerAdapter(ServerboundChatPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ServerboundChatSessionUpdatePacket", PacketLogConverter.registerAdapter(ServerboundChatSessionUpdatePacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ServerboundChunkBatchReceivedPacket", null);
        packet("ServerboundClientCommandPacket", null);
        packet("ServerboundCommandSuggestionPacket", PacketLogConverter.registerAdapter(ServerboundChatSessionUpdatePacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ServerboundConfigurationAcknowledgedPacket", null);
        packet("ServerboundContainerButtonClickPacket", null);
        packet("ServerboundContainerClickPacket", null);
        packet("ServerboundContainerClosePacket", null);
        packet("ServerboundContainerSlotStateChangedPacket", null);
        packet("ServerboundEditBookPacket", null);
        packet("ServerboundEntityTagQuery", null);
        packet("ServerboundInteractPacket", null);
        packet("ServerboundJigsawGeneratePacket", null);
        packet("ServerboundLockDifficultyPacket", null);
        packet("ServerboundMovePlayerPacket$Pos", null);
        packet("ServerboundMovePlayerPacket$PosRot", null);
        packet("ServerboundMovePlayerPacket$Rot", null);
        packet("ServerboundMovePlayerPacket$StatusOnly", null);
        packet("ServerboundMoveVehiclePacket", null);
        packet("ServerboundPaddleBoatPacket", null);
        packet("ServerboundPickItemPacket", null);
        packet("ServerboundPlaceRecipePacket", null);
        packet("ServerboundPlayerAbilitiesPacket", null);
        packet("ServerboundPlayerActionPacket", null);
        packet("ServerboundPlayerCommandPacket", null);
        packet("ServerboundPlayerInputPacket", null);
        packet("ServerboundRecipeBookChangeSettingsPacket", null);
        packet("ServerboundRecipeBookSeenRecipePacket", null);
        packet("ServerboundRenameItemPacket", null);
        packet("ServerboundSeenAdvancementsPacket", null);
        packet("ServerboundSelectTradePacket", null);
        packet("ServerboundSetBeaconPacket", null);
        packet("ServerboundSetCarriedItemPacket", null);
        packet("ServerboundSetCommandBlockPacket", null);
        packet("ServerboundSetCommandMinecartPacket", null);
        packet("ServerboundSetCreativeModeSlotPacket", null);
        packet("ServerboundSetJigsawBlockPacket", null);
        packet("ServerboundSetStructureBlockPacket", null);
        packet("ServerboundSignUpdatePacket", null);
        packet("ServerboundSwingPacket", null);
        packet("ServerboundTeleportToEntityPacket", null);
        packet("ServerboundUseItemOnPacket", null);
        packet("ServerboundUseItemPacket", null);

        if (!FMLEnvironment.production)
        {
            Set<String> typeNames = new HashSet<>(PacketLogHandler.getPacketTypeNames());
            Set<String> missingPackets = Sets.difference(typeNames, knownPackets);
            Set<String> invalidPackets = Sets.difference(knownPackets, typeNames);
            if (!missingPackets.isEmpty() || !invalidPackets.isEmpty())
            {
                throw new IllegalStateException(
                        "Registered packet set doesn't match vanilla packet set!" +
                        "\nPacket types: " + typeNames.size() +
                        "\n, known types: " + knownPackets.size() +
                        "\n, missing types: " + String.join(", ", missingPackets) +
                        "\n, invalid types: " + String.join(", ", invalidPackets)
                );
            }
        }

        PacketLogger.LOGGER.debug("Initialized log adapters for {} out of {} packets", specialCount, knownPackets.size());

        knownPackets = null;
    }

    private static void packet(String name, PacketLogAdapter<?> adapter)
    {
        knownPackets.add(name);
        if (adapter != null)
        {
            specialCount++;
        }
    }



    private PacketLogAdapters() { }
}
