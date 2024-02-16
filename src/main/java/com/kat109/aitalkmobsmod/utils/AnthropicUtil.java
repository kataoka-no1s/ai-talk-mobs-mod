package com.kat109.aitalkmobsmod.utils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.kat109.aitalkmobsmod.Config;
import com.kat109.aitalkmobsmod.software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import com.kat109.aitalkmobsmod.software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import com.kat109.aitalkmobsmod.software.amazon.awssdk.core.SdkBytes;
import com.kat109.aitalkmobsmod.software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import com.kat109.aitalkmobsmod.software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import com.kat109.aitalkmobsmod.software.amazon.awssdk.regions.Region;
import com.kat109.aitalkmobsmod.software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeAsyncClient;
import com.kat109.aitalkmobsmod.software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import com.kat109.aitalkmobsmod.software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public class AnthropicUtil {
	public static void send(LocalPlayer player, String mobName, String prompt) {
		if (StringUtils.isEmpty(Config.awsRegion) ||
				StringUtils.isEmpty(Config.awsKey)
				|| StringUtils.isEmpty(Config.awsSecretKey)) {
			player.sendSystemMessage(Component.nullToEmpty(
					ChatFormatting.RED
							+ "[ERROR]:Either AWS Region/AWS Key/AWS Secret Key is not set. Please use the commands \"/aiTalk awsConfig setAwsKey\", \"/aiTalk awsConfig setAwsSecretKey\", and \"/aiTalk awsConfig setAwsRegion\" to set them."));
			return;
		}

		String enclosedPrompt = "Human: " + prompt + "\n\nAssistant:";

		AwsBasicCredentials awsCreds = AwsBasicCredentials.create(Config.awsKey,
				Config.awsSecretKey);

		SdkAsyncHttpClient nettyHttpClient = NettyNioAsyncHttpClient.create();

		BedrockRuntimeAsyncClient client = BedrockRuntimeAsyncClient.builder()
				.region(Region.of(Config.awsRegion))
				.credentialsProvider(StaticCredentialsProvider.create(awsCreds))
				.httpClient(nettyHttpClient).build();

		String payload = new JSONObject().put("prompt", enclosedPrompt)
				.put("max_tokens_to_sample", Config.maxTokens).put("temperature", 0.5)
				.put("stop_sequences", List.of("\n\nHuman:")).toString();

		InvokeModelRequest request = InvokeModelRequest.builder().body(SdkBytes.fromUtf8String(payload))
				.modelId(Config.aiModel).contentType("application/json").accept("application/json")
				.build();

		CompletableFuture<InvokeModelResponse> completableFuture = client.invokeModel(request)
				.whenComplete((response, exception) -> {
					if (exception != null) {
						player.sendSystemMessage(
								Component.nullToEmpty(ChatFormatting.RED
										+ "[ERROR]:Model invocation failed: " + exception));
					}
				});

		String generatedText = "";
		InvokeModelResponse response;
		try {
			response = completableFuture.get();
			JSONObject responseBody = new JSONObject(response.body().asUtf8String());
			generatedText = responseBody.getString("completion");

			Config.saveChatMessage(mobName, generatedText);
		} catch (InterruptedException | ExecutionException e) {
			player.sendSystemMessage(Component.nullToEmpty(
					ChatFormatting.RED
							+ "[ERROR]:Failed to connect to AWS Bedrock, please check if AWS Region, AWS Key and AWS Secret Key are set correctly."));
			player.sendSystemMessage(Component.nullToEmpty(e.getMessage()));
		}
	}
}
