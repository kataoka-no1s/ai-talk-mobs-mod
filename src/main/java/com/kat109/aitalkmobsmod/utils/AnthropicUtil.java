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

/**
 * AnthropicのAIに関わるクラス（Claude2など）
 */
public class AnthropicUtil {
	/**
	 * Anthropicの生成AIのAPI接続処理
	 * 
	 * ※プロンプトをAPIにリクエストし、
	 * レスポンスのテキストをコンフィグファイルに保存する。
	 * 
	 * @param player  プレイヤー情報
	 * @param mobName モブの名前
	 * @param prompt  プロンプト
	 */
	public static void send(LocalPlayer player, String mobName, String prompt) {
		// APIキーがセットされているか確認
		if (StringUtils.isEmpty(Config.anthropicKey)) {
			player.sendSystemMessage(Component.nullToEmpty(
					ChatFormatting.RED
							+ "[ERROR]:API Key is not set. Please use the commands \"/aiTalk anthropicConfig setAPIKey\" to set it."));
			return;
		}

		// コンフィグファイルからAPIキーを取得
		String apiKey = Config.anthropicKey;

		OkHttpClient client = new OkHttpClient();

		// APIにリクエストするjsonオブジェクトを作成
		Gson gson = new Gson();
		Map<String, Object> requestBody = new HashMap<>();
		Map<String, String> userMsg = new HashMap<String, String>();
		userMsg.put("role", "user");
		userMsg.put("content", prompt);
		List<Map<String, String>> messages = new ArrayList<>();
		messages.add(userMsg);
		requestBody.put("model", Config.aiModel);
		requestBody.put("messages", messages);
		requestBody.put("max_tokens", Config.maxTokens);

		String json = gson.toJson(requestBody);

		// TODO:content-type jsonを指定すればこの処理は不要？
		com.squareup.okhttp.RequestBody body = com.squareup.okhttp.RequestBody.create(
				com.squareup.okhttp.MediaType.parse("application/json; charset=utf-8"),
				json);
		// リクエストボディを作成
		Request request = new Request.Builder()
				.url("https://api.anthropic.com/v1/messages")
				.post(body)
				.addHeader("anthropic-version", "2023-06-01")
				.addHeader("x-api-key", apiKey)
				.build();

		String generatedText = "";
		try {
			// リクエスト実行/レスポンス取得
			com.squareup.okhttp.Response response = client.newCall(request).execute();
			JSONObject responseBody = new JSONObject(response.body().string());

			// レスポンスからメッセージ部分を抽出
			generatedText = responseBody.getJSONArray("content").getJSONObject(0)
					.getString("text");
			// メッセージをコンフィグファイルに保存
			Config.saveChatMessage(mobName, generatedText);
		} catch (IOException e) {
			player.sendSystemMessage(Component.nullToEmpty(ChatFormatting.RED
					+ "[ERROR]:Failed to connect to OpenAI API, please check if API Key are set correctly."));
			player.sendSystemMessage(Component.nullToEmpty(e.getMessage()));
		}
	}
}
