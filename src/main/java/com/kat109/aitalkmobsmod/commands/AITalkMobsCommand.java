package com.kat109.aitalkmobsmod.commands;

import java.util.Arrays;

import com.kat109.aitalkmobsmod.Config;
import com.kat109.aitalkmobsmod.constants.Constants;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * コマンドクラス
 */
public class AITalkMobsCommand {
	/**
	 * コマンドを登録
	 * 
	 * @param dispatcher
	 */
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("aiTalk") // 各コマンドの最初に「/aiTalk」をつける
				.then(Commands.literal("info").executes(context -> {
					/**
					 * 「/aiTalk info」コマンド
					 * ・ 現在の登録情報を表示する
					 */

					String infoString = """
							AI Model: %s
							Talk Language: %s
							""".formatted(Config.aiModel, Config.talkLanguage);

					String model = Config.aiModel;
					if (Arrays.asList(Constants.OPENAI_MODELS).contains(model)) {

						infoString += """
								API Key: %s
								""".formatted((!Config.openAIKey.isEmpty() ? "configured" : "unset"));
					} else if (Arrays.asList(Constants.ANTHROPIC_MODELS).contains(model)) {

						infoString += """
								API Key: %s
								""".formatted((!Config.anthropicKey.isEmpty() ? "configured" : "unset"));
					} else if (Arrays.asList(Constants.AWS_BEDROCK_MODELS).contains(model)) {
						infoString += """
								AWS Region Key: %s
								AWS Key: %s
								AWS Secret Key: %s
								""".formatted(Config.awsRegion,
								(!Config.awsKey.isEmpty() ? "configured" : "unset"),
								(!Config.awsSecretKey.isEmpty() ? "configured" : "unset"));
					} else if (Arrays.asList(Constants.GOOGLEAI_MODELS).contains(model)) {

						infoString += """
								API Key: %s
								""".formatted((!Config.googleAIKey.isEmpty() ? "configured" : "unset"));
					}

					context.getSource().getPlayerOrException()
							.sendSystemMessage(Component.nullToEmpty(infoString));
					return Command.SINGLE_SUCCESS;
				}))
				.then(Commands.literal("selectModel")
						/**
						 * 「/aiTalk selectModel {モデル名}」コマンド
						 * ・ 利用するモデルを選択する
						 */

						.then(Commands.literal("gpt-3.5-turbo").executes(context -> {
							return saveModelAndsendMessage(context, "gpt-3.5-turbo",
									Constants.OPENAI_MODEL_GPT3_5_TURBO);
						})).then(Commands.literal("gpt-4").executes(context -> {
							return saveModelAndsendMessage(context, "gpt-4",
									Constants.OPENAI_MODEL_GPT4);
						})).then(Commands.literal("gpt-4-turbo-preview").executes(context -> {
							return saveModelAndsendMessage(context, "gpt-4-turbo-preview",
									Constants.OPENAI_MODEL_GPT4_TURBO_PREVIEW);
						})).then(Commands.literal("claude-2.0").executes(context -> {
							return saveModelAndsendMessage(context, "claude-2.0",
									Constants.ANTHROPIC_MODEL_CLAUDE2);
						})).then(Commands.literal("claude-2.1").executes(context -> {
							return saveModelAndsendMessage(context, "claude-2.1",
									Constants.ANTHROPIC_MODEL_CLAUDE2_1);
						})).then(Commands.literal("claude-3-opus").executes(context -> {
							return saveModelAndsendMessage(context, "claude-3-opus",
									Constants.ANTHROPIC_MODEL_CLAUDE3_OPUS);
						})).then(Commands.literal("claude-3-sonnet").executes(context -> {
							return saveModelAndsendMessage(context, "claude-3-sonnet",
									Constants.ANTHROPIC_MODEL_CLAUDE3_SONNET);
						})).then(Commands.literal("claude-3-haiku").executes(context -> {
							return saveModelAndsendMessage(context, "claude-3-haiku",
									Constants.ANTHROPIC_MODEL_CLAUDE3_HAIKU);
						})).then(Commands.literal("claude-2.0(AwsBedrock)").executes(context -> {
							return saveModelAndsendMessage(context, "claude-2.0(AwsBedrock)",
									Constants.AWS_BEDROCK_MODEL_CLAUDE2);
						})).then(Commands.literal("claude-2.1(AwsBedrock)").executes(context -> {
							return saveModelAndsendMessage(context, "claude-2.1(AwsBedrock)",
									Constants.AWS_BEDROCK_MODEL_CLAUDE2_1);
						})).then(Commands.literal("gemini-pro").executes(context -> {
							return saveModelAndsendMessage(context, "gemini-pro",
									Constants.GOOGLE_AI_MODEL_GEMINI_PRO);
						})))
				.then(Commands.literal("selectTalkLanguage")
						/**
						 * 「/aiTalk selectTalkLanguage {言語}」コマンド
						 * ・ モブが話す言語を選択する
						 */

						.then(Commands.literal("english").executes(context -> {
							Config.saveTalkLanguage("english");
							Config.resetChatMessage();
							context.getSource().getPlayerOrException()
									.sendSystemMessage(
											Component.nullToEmpty(" The language used by the mob is set to English."));
							return Command.SINGLE_SUCCESS;
						})).then(Commands.literal("japanese").executes(context -> {
							Config.saveTalkLanguage("japanese");
							Config.resetChatMessage();
							context.getSource().getPlayerOrException()
									.sendSystemMessage(Component.nullToEmpty(" モブが話す言語を日本語に設定しました。"));
							return Command.SINGLE_SUCCESS;
						})).then(Commands.literal("korean").executes(context -> {
							Config.saveTalkLanguage("korean");
							Config.resetChatMessage();
							context.getSource().getPlayerOrException()
									.sendSystemMessage(Component.nullToEmpty(" 몹이 사용하는 언어를 한국어로 설정했습니다."));
							return Command.SINGLE_SUCCESS;
						})).then(Commands.literal("chinese").executes(context -> {
							Config.saveTalkLanguage("chinese");
							Config.resetChatMessage();
							context.getSource().getPlayerOrException()
									.sendSystemMessage(Component.nullToEmpty(" 暴徒使用的语言设置为中文。"));
							return Command.SINGLE_SUCCESS;
						})))
				.then(Commands.literal("openAIConfig")
						.then(Commands.literal("setApiKey")
								/**
								 * 「/aiTalk openAIConfig setApiKey {APIキー}」コマンド
								 * ・ OpenAIのAPIキーを登録する
								 */

								.then(Commands.argument("apiKey", StringArgumentType.string()).executes(context -> {
									String apiKey = context.getArgument("apiKey", String.class);
									Config.saveOpenAIKey(apiKey);
									context.getSource().getPlayerOrException()
											.sendSystemMessage(Component.nullToEmpty(" API Key is set."));
									return Command.SINGLE_SUCCESS;
								}))))
				.then(Commands.literal("anthropicConfig")
						.then(Commands.literal("setApiKey")
								/**
								 * 「/aiTalk anthropicConfig setApiKey {APIキー}」コマンド
								 * ・ AnthropicのAPIキーを登録する
								 */

								.then(Commands.argument("apiKey", StringArgumentType.string()).executes(context -> {
									String apiKey = context.getArgument("apiKey", String.class);
									Config.saveAnthropicKey(apiKey);
									context.getSource().getPlayerOrException()
											.sendSystemMessage(Component.nullToEmpty(" API Key is set."));
									return Command.SINGLE_SUCCESS;
								}))))
				.then(Commands.literal("awsConfig")
						.then(Commands.literal("setAwsKey")
								/**
								 * 「/aiTalk awsConfig setAwsKey {AWSアクセスキー}」コマンド
								 * ・ AWS Bedrockを利用するAIの場合にAWSアクセスキーを登録する
								 */

								.then(Commands.argument("awsKey", StringArgumentType.string()).executes(context -> {
									String awsKey = context.getArgument("awsKey", String.class);
									Config.saveAwsKey(awsKey);
									context.getSource().getPlayerOrException()
											.sendSystemMessage(Component.nullToEmpty(" AWS Key is set."));
									return Command.SINGLE_SUCCESS;
								})))
						.then(Commands.literal("setAwsSecretKey")
								/**
								 * 「/aiTalk awsConfig setAwsSecretKey {AWSシークレットキー}」コマンド
								 * ・ AWS Bedrockを利用するAIの場合にAWSシークレットキーを登録する
								 */

								.then(Commands.argument("awsSecretKey", StringArgumentType.string())
										.executes(context -> {
											String awsSecretKey = context.getArgument("awsSecretKey", String.class);
											Config.saveAwsSecretKey(awsSecretKey);
											context.getSource().getPlayerOrException()
													.sendSystemMessage(
															Component.nullToEmpty(" AWS Secret Key is set."));
											return Command.SINGLE_SUCCESS;
										})))
						.then(Commands.literal("setAwsRegion")
								/**
								 * 「/aiTalk awsConfig setAwsRegion {AWSリージョン}」コマンド
								 * ・ AWS Bedrockを利用するAIの場合にAWSリージョンを登録する
								 */

								.then(Commands.argument("awsRegion", StringArgumentType.string()).executes(context -> {
									String awsRegion = context.getArgument("awsRegion", String.class);
									Config.saveAwsRegion(awsRegion);
									context.getSource().getPlayerOrException()
											.sendSystemMessage(Component.nullToEmpty(" AWS Region is set."));
									return Command.SINGLE_SUCCESS;
								}))))
				.then(Commands.literal("googleAIConfig")
						.then(Commands.literal("setApiKey")
								/**
								 * 「/aiTalk googleAIConfig setApiKey {APIキー}」コマンド
								 * ・ GoogleAIのAPIキーを登録する
								 */

								.then(Commands.argument("apiKey", StringArgumentType.string()).executes(context -> {
									String apiKey = context.getArgument("apiKey", String.class);
									Config.saveGoogleAIKey(apiKey);
									context.getSource().getPlayerOrException()
											.sendSystemMessage(Component.nullToEmpty(" API Key is set."));
									return Command.SINGLE_SUCCESS;
								})))));
	}

	/**
	 * 使用するモデルを保存して完了メッセージを表示する
	 * 
	 * @param context       CommandContext
	 * @param dispModelName 表示用モデル名
	 * @param modelName     実際のモデル名（APIリクエスト時に使用）
	 * @return
	 * @throws CommandSyntaxException
	 */
	public static int saveModelAndsendMessage(CommandContext<CommandSourceStack> context, String dispModelName,
			String modelName) throws CommandSyntaxException {
		Config.saveAIModel(modelName);
		context.getSource().getPlayerOrException()
				.sendSystemMessage(
						Component.nullToEmpty(" The AI model was set to " + dispModelName + "."));
		return Command.SINGLE_SUCCESS;
	}
}
