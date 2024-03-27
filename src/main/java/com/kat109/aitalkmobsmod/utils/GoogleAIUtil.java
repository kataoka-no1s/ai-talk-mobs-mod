package com.kat109.aitalkmobsmod.utils;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.kat109.aitalkmobsmod.Config;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import net.minecraft.ChatFormatting;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;

/**
 * GoogleのAIに関わるクラス（Geminiなど）
 */
public class GoogleAIUtil {
	/**
	 * Googleの生成AIのAPI接続処理
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
		if (StringUtils.isEmpty(Config.googleAIKey)) {
			player.sendSystemMessage(Component.nullToEmpty(
					ChatFormatting.RED
							+ "[ERROR]:API Key is not set. Please use the commands \"/aiTalk googleAIConfig setAPIKey\" to set it."));
			return;
		}

		// コンフィグファイルからAPIキーを取得
		String apiKey = Config.googleAIKey;

		OkHttpClient client = new OkHttpClient();

		JsonObject message = new JsonObject();

		// APIにリクエストするjsonオブジェクトを作成
		message.addProperty("text", prompt);
		JsonObject contents = new JsonObject();
		contents.add("parts", new JsonArray());
		contents.get("parts").getAsJsonArray().add(message);
		JsonObject json = new JsonObject();
		json.add("contents", new JsonArray());
		json.get("contents").getAsJsonArray().add(contents);

		// リクエストボディを作成
		RequestBody body = RequestBody.create(
				MediaType.parse("application/json; charset=utf-8"),
				json.toString());

		com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
				.url("https://generativelanguage.googleapis.com/v1/models/" + Config.aiModel + ":generateContent")
				.post(body)
				.addHeader("x-goog-api-key", apiKey)
				.addHeader("Content-Type", "application/json")
				.addHeader("Accept", "application/json")
				.build();
		try {
			// リクエスト実行/レスポンス取得
			Response response = client.newCall(request).execute();
			JSONObject responseBody = new JSONObject(response.body().string());

			// レスポンスからメッセージ部分を抽出
			String generatedText = responseBody.getJSONArray("candidates").getJSONObject(0).getJSONObject("content")
					.getJSONArray("parts")
					.getJSONObject(0).getString("text");

			// メッセージをコンフィグファイルに保存
			Config.saveChatMessage(mobName, generatedText);
		} catch (IOException e) {
			player.sendSystemMessage(Component.nullToEmpty(ChatFormatting.RED
					+ "[ERROR]:Failed to connect to Google AI API, please check if API Key are set correctly."));
			player.sendSystemMessage(Component.nullToEmpty(e.getMessage()));
		}
	}
}
