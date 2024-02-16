package com.kat109.aitalkmobsmod.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.kat109.aitalkmobsmod.Config;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

public class OpenAIUtil {
	public static void send(LocalPlayer player, String mobName, String prompt) {
		if (StringUtils.isEmpty(Config.openAIKey)) {
			player.sendSystemMessage(Component.nullToEmpty(
					ChatFormatting.RED
							+ "[ERROR]:API Key is not set. Please use the commands \"/aiTalk openAIConfig setAPIKey\" to set it."));
			return;
		}

		String apiKey = Config.openAIKey;
		OkHttpClient client = new OkHttpClient();

		Gson gson = new Gson();

		Map<String, Object> requestBody = new HashMap<>();
		Map<String, String> systemMsg = new HashMap<String, String>();
		systemMsg.put("role", "system");
		systemMsg.put("content", "You are a helpful assistant.");
		Map<String, String> userMsg = new HashMap<String, String>();
		userMsg.put("role", "user");
		userMsg.put("content", prompt);
		List<Map<String, String>> messages = new ArrayList<>();
		messages.add(systemMsg);
		messages.add(userMsg);
		requestBody.put("model", Config.aiModel);
		requestBody.put("messages", messages);
		requestBody.put("max_tokens", Config.maxTokens);

		String jsonRequestBody = gson.toJson(requestBody);

		com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(
				com.squareup.okhttp.MediaType.parse("application/json; charset=utf-8"),
				jsonRequestBody);

		Request request = new Request.Builder()
				.url("https://api.openai.com/v1/chat/completions")
				.post(body)
				.addHeader("Authorization", "Bearer " + apiKey)
				.build();

		String generatedText = "";
		try {
			com.squareup.okhttp.Response response = client.newCall(request).execute();
			JSONObject responseBody = new JSONObject(response.body().string());
			generatedText = responseBody.getJSONArray("choices").getJSONObject(0)
					.getJSONObject("message").getString("content");
			Config.saveChatMessage(mobName, generatedText);
		} catch (IOException e) {
			player.sendSystemMessage(Component.nullToEmpty(ChatFormatting.RED
					+ "[ERROR]:Failed to connect to OpenAI API, please check if API Key are set correctly."));
			player.sendSystemMessage(Component.nullToEmpty(e.getMessage()));
		}
	}
}
