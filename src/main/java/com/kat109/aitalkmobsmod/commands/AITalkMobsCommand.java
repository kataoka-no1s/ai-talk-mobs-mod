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

public class AITalkMobsCommand {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("aiTalk")
				.then(Commands.literal("info").executes(context -> {

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
								AWS Region Key: %s
								AWS Key: %s
								AWS Secret Key: %s
								""".formatted(Config.awsRegion,
								(!Config.awsKey.isEmpty() ? "configured" : "unset"),
								(!Config.awsSecretKey.isEmpty() ? "configured" : "unset"));
					}

					context.getSource().getPlayerOrException()
							.sendSystemMessage(Component.nullToEmpty(infoString));
					return Command.SINGLE_SUCCESS;
				}))
				.then(Commands.literal("selectModel")
						.then(Commands.literal("gpt-3.5-turbo").executes(context -> {
							return sendModelChangeMessage(context, "gpt-3.5-turbo",
									Constants.OPENAI_MODEL_GPT3_5_TURBO);
						})).then(Commands.literal("gpt-4").executes(context -> {
							return sendModelChangeMessage(context, "gpt-4",
									Constants.OPENAI_MODEL_GPT4);
						})).then(Commands.literal("gpt-4-turbo-preview").executes(context -> {
							return sendModelChangeMessage(context, "gpt-4-turbo-preview",
									Constants.OPENAI_MODEL_GPT4_TURBO_PREVIEW);
						})).then(Commands.literal("claude-2.0").executes(context -> {
							return sendModelChangeMessage(context, "claude-2.0",
									Constants.ANTHROPIC_MODEL_CLAUDE2);
						})).then(Commands.literal("claude-2.1").executes(context -> {
							return sendModelChangeMessage(context, "claude-2.1",
									Constants.ANTHROPIC_MODEL_CLAUDE2_1);
						})))
				.then(Commands.literal("selectTalkLanguage")
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
								.then(Commands.argument("apiKey", StringArgumentType.string()).executes(context -> {
									String apiKey = context.getArgument("apiKey", String.class);
									Config.saveOpenAIKey(apiKey);
									context.getSource().getPlayerOrException()
											.sendSystemMessage(Component.nullToEmpty(" API Key is set."));
									return Command.SINGLE_SUCCESS;
								}))))
				.then(Commands.literal("awsConfig")
						.then(Commands.literal("setAwsKey")
								.then(Commands.argument("awsKey", StringArgumentType.string()).executes(context -> {
									String awsKey = context.getArgument("awsKey", String.class);
									Config.saveAwsKey(awsKey);
									context.getSource().getPlayerOrException()
											.sendSystemMessage(Component.nullToEmpty(" AWS Key is set."));
									return Command.SINGLE_SUCCESS;
								})))
						.then(Commands.literal("setAwsSecretKey")
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
								.then(Commands.argument("awsRegion", StringArgumentType.string()).executes(context -> {
									String awsRegion = context.getArgument("awsRegion", String.class);
									Config.saveAwsRegion(awsRegion);
									context.getSource().getPlayerOrException()
											.sendSystemMessage(Component.nullToEmpty(" AWS Region is set."));
									return Command.SINGLE_SUCCESS;
								})))));
	}

	public static int sendModelChangeMessage(CommandContext<CommandSourceStack> context, String dispModelName,
			String modelName) throws CommandSyntaxException {
		Config.saveAIModel(modelName);
		context.getSource().getPlayerOrException()
				.sendSystemMessage(
						Component.nullToEmpty(" The AI model was set to " + dispModelName + "."));
		return Command.SINGLE_SUCCESS;
	}
}
