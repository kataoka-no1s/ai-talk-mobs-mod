package com.kat109.aitalkmobsmod;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import com.kat109.aitalkmobsmod.commands.AITalkMobsCommand;
import com.kat109.aitalkmobsmod.constants.Constants;
import com.kat109.aitalkmobsmod.utils.AnthropicUtil;
import com.kat109.aitalkmobsmod.utils.OpenAIUtil;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Constants.MODID)
public class AITalkMobsMod {

	public AITalkMobsMod() {
		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);

		// Register our mod's ForgeConfigSpec so that Forge can create and load the
		// config file for us
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
	}

	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteract event)
			throws UncheckedIOException, URISyntaxException, InterruptedException, ExecutionException {
		// クライアントサイドかどうかの判定はisRemoteから以下に変わっていた。。
		// 参考：https://docs.minecraftforge.net/en/1.20.x/concepts/sides/
		if (!event.getLevel().isClientSide()) {
			return;
		}

		try {
			if (event.getTarget() instanceof Mob) {
				// エンティティがモブだった場合

				Mob entity = (Mob) event.getTarget();

				if (entity.isAlive() && event.getHand() == InteractionHand.MAIN_HAND) {
					// モブが生きているかつ右クリックされた場合

					// プレイヤー取得
					LocalPlayer player = Minecraft.getInstance().player;

					// モブの名前取得
					String mobName = entity.getDisplayName().getString();

					// AIからメッセージを取得するスレッド関数
					Thread connectAI = new Thread(() -> {
						// プロンプト定義
						String prompt = Config.prompt;
						switch (Config.talkLanguage) {
							case "japanese":
								prompt = Config.promptJP;
								break;
							case "korean":
								prompt = Config.promptKR;
								break;
							case "chinese":
								prompt = Config.promptCH;
								break;
							default:
								break;
						}
						prompt = prompt.replace("{mobName}", mobName);

						// AIのモデル
						String model = Config.aiModel;
						if (Arrays.asList(Constants.OPENAI_MODELS).contains(model)) {
							OpenAIUtil.send(player, mobName, prompt);
						} else if (Arrays.asList(Constants.ANTHROPIC_MODELS).contains(model)) {
							AnthropicUtil.send(player, mobName, prompt);
						} else {
							player.sendSystemMessage(Component.nullToEmpty(
									ChatFormatting.RED
											+ "[ERROR]:Select the correct model of AI with the \"/aiTalk selectModel\" command."));
							return;
						}
					});

					// 非同期でAIからメッセージを取得
					connectAI.start();

					event.setCanceled(true);

					// モブのメッセージを抽出
					String message = Config.chatMessages.get(mobName);

					if (message == null)
						// デフォルトのメッセージ
						switch (Config.talkLanguage) {
							case "japanese":
								message = "こんにちは！";
								break;
							case "korean":
								message = "안녕하세요！";
								break;
							case "chinese":
								message = "下午好！";
								break;
							default:
								message = "Hello!";
								break;
						}

					// メッセージを表示
					player.sendSystemMessage(Component.nullToEmpty(mobName + '：' + message));
				}
			}
		} catch (

		Exception ex) {
			ex.printStackTrace();
		}
	}

	// コマンドを登録
	@SubscribeEvent
	public void onRegisterCommandEvent(RegisterCommandsEvent event) {
		CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();
		AITalkMobsCommand.register(commandDispatcher);
	}
}
