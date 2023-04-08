package xfacthd.packetlogger.logger;

import com.google.common.collect.Sets;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.protocol.login.*;
import xfacthd.packetlogger.PacketLogger;
import xfacthd.packetlogger.logger.adapters.*;

import java.util.*;

public final class PacketLogAdapters
{
    private static Set<String> knownPackets = new HashSet<>(PacketLogHandler.getPacketTypeNames().size());
    private static int specialCount = 0;

    public static void init()
    {
        packet("ClientIntentionPacket", null);
        packet("ClientboundAddEntityPacket", PacketLogConverter.registerAdapter(ClientboundAddEntityPacket.class, new ClientboundAddEntityPacketLogAdapter.Entity()));
        packet("ClientboundAddExperienceOrbPacket", null);
        packet("ClientboundAddPlayerPacket", PacketLogConverter.registerAdapter(ClientboundAddPlayerPacket.class, new ClientboundAddEntityPacketLogAdapter.Player()));
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
        packet("ClientboundChunksBiomesPacket", null);
        packet("ClientboundClearTitlesPacket", null);
        packet("ClientboundCommandSuggestionsPacket", null);
        packet("ClientboundCommandsPacket", null);
        packet("ClientboundContainerClosePacket", null);
        packet("ClientboundContainerSetContentPacket", null);
        packet("ClientboundContainerSetDataPacket", null);
        packet("ClientboundContainerSetSlotPacket", null);
        packet("ClientboundCooldownPacket", null);
        packet("ClientboundCustomChatCompletionsPacket", null);
        packet("ClientboundCustomPayloadPacket", PacketLogConverter.registerAdapter(ClientboundCustomPayloadPacket.class, new CustomPayloadPacketLogAdapter.Clientbound()));
        packet("ClientboundCustomQueryPacket", null);
        packet("ClientboundDamageEventPacket", null);
        packet("ClientboundDeleteChatPacket", PacketLogConverter.registerAdapter(ClientboundDeleteChatPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ClientboundDisconnectPacket", null);
        packet("ClientboundDisguisedChatPacket", null);
        packet("ClientboundEntityEventPacket", null);
        packet("ClientboundExplodePacket", null);
        packet("ClientboundForgetLevelChunkPacket", null);
        packet("ClientboundGameEventPacket", null);
        packet("ClientboundGameProfilePacket", null);
        packet("ClientboundHelloPacket", PacketLogConverter.registerAdapter(ClientboundHelloPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ClientboundHorseScreenOpenPacket", null);
        packet("ClientboundHurtAnimationPacket", null);
        packet("ClientboundInitializeBorderPacket", null);
        packet("ClientboundKeepAlivePacket", null);
        packet("ClientboundLevelChunkWithLightPacket", PacketLogConverter.registerAdapter(ClientboundLevelChunkWithLightPacket.class, new ClientboundLevelChunkWithLightPacketLogAdapter()));
        packet("ClientboundLevelEventPacket", null);
        packet("ClientboundLevelParticlesPacket", null);
        packet("ClientboundLightUpdatePacket", null);
        packet("ClientboundLoginCompressionPacket", null);
        packet("ClientboundLoginDisconnectPacket", null);
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
        packet("ClientboundPingPacket", null);
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
        packet("ClientboundPongResponsePacket", null);
        packet("ClientboundRecipePacket", null);
        packet("ClientboundRemoveEntitiesPacket", null);
        packet("ClientboundRemoveMobEffectPacket", null);
        packet("ClientboundResourcePackPacket", null);
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
        packet("ClientboundSetTitleTextPacket", null);
        packet("ClientboundSetTitlesAnimationPacket", null);
        packet("ClientboundSoundEntityPacket", null);
        packet("ClientboundSoundPacket", null);
        packet("ClientboundStatusResponsePacket", null);
        packet("ClientboundStopSoundPacket", null);
        packet("ClientboundSystemChatPacket", null);
        packet("ClientboundTabListPacket", null);
        packet("ClientboundTagQueryPacket", null);
        packet("ClientboundTakeItemEntityPacket", null);
        packet("ClientboundTeleportEntityPacket", null);
        packet("ClientboundUpdateAdvancementsPacket", null);
        packet("ClientboundUpdateAttributesPacket", null);
        packet("ClientboundUpdateEnabledFeaturesPacket", null);
        packet("ClientboundUpdateMobEffectPacket", null);
        packet("ClientboundUpdateRecipesPacket", PacketLogConverter.registerAdapter(ClientboundUpdateRecipesPacket.class, new ClientboundUpdateDatapackDataPacketLogAdapter.Recipes()));
        packet("ClientboundUpdateTagsPacket", PacketLogConverter.registerAdapter(ClientboundUpdateTagsPacket.class, new ClientboundUpdateDatapackDataPacketLogAdapter.Tags()));
        packet("ServerboundAcceptTeleportationPacket", null);
        packet("ServerboundBlockEntityTagQuery", null);
        packet("ServerboundChangeDifficultyPacket", null);
        packet("ServerboundChatAckPacket", null);
        packet("ServerboundChatCommandPacket", PacketLogConverter.registerAdapter(ServerboundChatCommandPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ServerboundChatPacket", PacketLogConverter.registerAdapter(ServerboundChatPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ServerboundChatSessionUpdatePacket", PacketLogConverter.registerAdapter(ServerboundChatSessionUpdatePacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ServerboundClientCommandPacket", null);
        packet("ServerboundClientInformationPacket", null);
        packet("ServerboundCommandSuggestionPacket", PacketLogConverter.registerAdapter(ServerboundChatSessionUpdatePacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ServerboundContainerButtonClickPacket", null);
        packet("ServerboundContainerClickPacket", null);
        packet("ServerboundContainerClosePacket", null);
        packet("ServerboundCustomPayloadPacket", PacketLogConverter.registerAdapter(ServerboundCustomPayloadPacket.class, new CustomPayloadPacketLogAdapter.Serverbound()));
        packet("ServerboundCustomQueryPacket", null);
        packet("ServerboundEditBookPacket", null);
        packet("ServerboundEntityTagQuery", null);
        packet("ServerboundHelloPacket", null);
        packet("ServerboundInteractPacket", null);
        packet("ServerboundJigsawGeneratePacket", null);
        packet("ServerboundKeepAlivePacket", null);
        packet("ServerboundKeyPacket", PacketLogConverter.registerAdapter(ServerboundKeyPacket.class, SensitiveDataPacketLogAdapter.INSTANCE));
        packet("ServerboundLockDifficultyPacket", null);
        packet("ServerboundMovePlayerPacket$Pos", null);
        packet("ServerboundMovePlayerPacket$PosRot", null);
        packet("ServerboundMovePlayerPacket$Rot", null);
        packet("ServerboundMovePlayerPacket$StatusOnly", null);
        packet("ServerboundMoveVehiclePacket", null);
        packet("ServerboundPaddleBoatPacket", null);
        packet("ServerboundPickItemPacket", null);
        packet("ServerboundPingRequestPacket", null);
        packet("ServerboundPlaceRecipePacket", null);
        packet("ServerboundPlayerAbilitiesPacket", null);
        packet("ServerboundPlayerActionPacket", null);
        packet("ServerboundPlayerCommandPacket", null);
        packet("ServerboundPlayerInputPacket", null);
        packet("ServerboundPongPacket", null);
        packet("ServerboundRecipeBookChangeSettingsPacket", null);
        packet("ServerboundRecipeBookSeenRecipePacket", null);
        packet("ServerboundRenameItemPacket", null);
        packet("ServerboundResourcePackPacket", null);
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
        packet("ServerboundStatusRequestPacket", null);
        packet("ServerboundSwingPacket", null);
        packet("ServerboundTeleportToEntityPacket", null);
        packet("ServerboundUseItemOnPacket", null);
        packet("ServerboundUseItemPacket", null);

        int packetCount = PacketLogHandler.getPacketTypeNames().size();
        if (knownPackets.size() != packetCount)
        {
            String missingPackets = String.join(", ", Sets.difference(
                    new HashSet<>(PacketLogHandler.getPacketTypeNames()),
                    knownPackets
            ));
            throw new IllegalStateException(
                    "Packet types: " + packetCount +
                    ", known types: " + knownPackets.size() +
                    ", missing types: " + missingPackets
            );
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
