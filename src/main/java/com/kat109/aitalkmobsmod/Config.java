package com.kat109.aitalkmobsmod;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kat109.aitalkmobsmod.constants.Constants;

/**
 * コンフィグクラス
 * 
 * このクラスの内容を元にコンフィグファイルが生成される。
 */
@Mod.EventBusSubscriber(modid = Constants.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

	// AIのモデル。デフォルトは「gpt-3.5-turbo」としている。
	public static final ForgeConfigSpec.ConfigValue<String> AI_MODEL = BUILDER.comment("AI Model")
			.define("aiModel", Constants.OPENAI_MODEL_GPT3_5_TURBO);

	// モブが話す言語。デフォルトは英語としている。
	public static final ForgeConfigSpec.ConfigValue<String> TALK_LANGUAGE = BUILDER
			.comment("Language when talking to mobs").define("talkLanguage", "english");

	// OpenAIのAPIキー
	public static final ForgeConfigSpec.ConfigValue<String> OPENAI_API_KEY = BUILDER.comment("OpenAI API Key")
			.define("openAIKey", "");

	// GoogleAIのAPIキー
	public static final ForgeConfigSpec.ConfigValue<String> GOOGLEAI_API_KEY = BUILDER.comment("GoogleAI API Key")
			.define("googleAIKey", "");

	// AnthropicのAPIキー
	public static final ForgeConfigSpec.ConfigValue<String> ANTHROPIC_API_KEY = BUILDER.comment("Anthropic API Key")
			.define("anthropicKey", "");

	// AWSリージョン
	public static final ForgeConfigSpec.ConfigValue<String> AWS_REGION = BUILDER.comment("AWS region")
			.define("awsRegion", "");

	// AWSアクセスキー
	public static final ForgeConfigSpec.ConfigValue<String> AWS_KEY = BUILDER.comment("AWS access key id")
			.define("awsKey", "");

	// AWSシークレットキー
	public static final ForgeConfigSpec.ConfigValue<String> AWS_SECRET_KEY = BUILDER.comment("AWS secret key")
			.define("awsSecretKey", "");

	// 最大トークン数（多すぎると料金が高くなる可能性がある）
	public static final ForgeConfigSpec.ConfigValue<Number> MAX_TOKENS = BUILDER.comment("Maximum number of AI tokens")
			.define("maxTokens", 150);

	// プロンプト（英語）
	public static final ForgeConfigSpec.ConfigValue<String> PROMPT = BUILDER
			.comment("AI Prompt. The mob name section should be {mobName}.").define("prompt",
					"Pretend to be a Minecraft '{mobName}' and greet them in 20 words or less, in a rough, spoken. Do not use violent words such as 'kill.'");

	// プロンプト（日本語）
	public static final ForgeConfigSpec.ConfigValue<String> PROMPT_JP = BUILDER
			.comment("AI のプロンプト。 モブ名の部分は{mobName}としてください。")
			.define("promptJP", "マインクラフトの「{mobName}」になりきって30文字以内で挨拶して。ラフな話し方で。ひらがなで。「殺す」などの暴力的な言葉は使わないで。");

	// プロンプト（韓国語）
	public static final ForgeConfigSpec.ConfigValue<String> PROMPT_KR = BUILDER
			.comment("AI 프롬프트. 몹 이름 부분은 {mobName}으로 입력하세요.")
			.define("promptKR", "마인크래프트의 '{mobName}'이 되어 20자 이내로 인사해 보세요. '죽여라' 등 폭력적인 단어는 사용하지 않는다.");

	// プロンプト（中国語）
	public static final ForgeConfigSpec.ConfigValue<String> PROMPT_CH = BUILDER.comment("AI 提示。暴民名称部分应为 {mobName}。")
			.define("promptCH",
					"假装自己是 Minecraft 中的\"{mobName}\"，用 20 个字符或更少的文字打招呼。 使用粗略的说话方式。 中文。");

	// モブのメッセージ（API接続に時間を要するため、右クリック時に一度メッセージをコンフィグに保存し、次に右クリックした際にそのメッセージを表示することで直ぐにメッセージが表示されるようにしている）
	private static final ForgeConfigSpec.ConfigValue<List<? extends String>> CHAT_MESSAGES = BUILDER
			.comment("Chat messages")
			.defineList("chatMessages", Arrays.asList("default@@default"), (chatMessage) -> true);

	static final ForgeConfigSpec SPEC = BUILDER.build();

	public static String aiModel;
	public static String talkLanguage;
	public static String awsRegion;
	public static String openAIKey;
	public static String anthropicKey;
	public static String awsKey;
	public static String awsSecretKey;
	public static String googleAIKey;
	public static Number maxTokens;
	public static String prompt;
	public static String promptJP;
	public static String promptKR;
	public static String promptCH;
	public static Map<String, String> chatMessages;

	@SubscribeEvent
	static void onLoad(final ModConfigEvent event) {
		aiModel = AI_MODEL.get();
		talkLanguage = TALK_LANGUAGE.get();
		awsRegion = AWS_REGION.get();
		openAIKey = OPENAI_API_KEY.get();
		anthropicKey = ANTHROPIC_API_KEY.get();
		awsKey = AWS_KEY.get();
		awsSecretKey = AWS_SECRET_KEY.get();
		googleAIKey = GOOGLEAI_API_KEY.get();
		maxTokens = MAX_TOKENS.get();
		prompt = PROMPT.get();
		promptJP = PROMPT_JP.get();
		promptKR = PROMPT_KR.get();
		promptCH = PROMPT_CH.get();

		chatMessages = new HashMap<String, String>();
		for (String chatMessage : CHAT_MESSAGES.get()) {
			String[] splitMessage = chatMessage.split("@@");
			// MapにListのキーと値を追加
			chatMessages.put(splitMessage[0], splitMessage[1]);
		}
	}

	/**
	 * AIのモデル保存処理
	 * 
	 * @param aiModel AIモデル名
	 */
	public static void saveAIModel(final String aiModel) {
		AI_MODEL.set(aiModel);
		AI_MODEL.save();
	}

	/**
	 * モブの言語保存処理
	 * 
	 * @param talkLanguage モブの言語
	 */
	public static void saveTalkLanguage(final String talkLanguage) {
		TALK_LANGUAGE.set(talkLanguage);
		TALK_LANGUAGE.save();
	}

	/**
	 * AWSリージョン保存処理
	 * 
	 * @param awsRegion AWSリージョン
	 */
	public static void saveAwsRegion(final String awsRegion) {
		AWS_REGION.set(awsRegion);
		AWS_REGION.save();
	}

	/**
	 * OpenAIのAPIキー保存処理
	 * 
	 * @param key APIキー
	 */
	public static void saveOpenAIKey(final String key) {
		OPENAI_API_KEY.set(key);
		OPENAI_API_KEY.save();
	}

	/**
	 * AnthropicのAPIキー保存処理
	 * 
	 * @param key APIキー
	 */
	public static void saveAnthropicKey(final String key) {
		ANTHROPIC_API_KEY.set(key);
		ANTHROPIC_API_KEY.save();
	}

	/**
	 * AWSアクセスキー保存処理
	 * 
	 * @param key AWSアクセスキー
	 */
	public static void saveAwsKey(final String key) {
		AWS_KEY.set(key);
		AWS_KEY.save();
	}

	/**
	 * AWSシークレットキー保存処理
	 * 
	 * @param awsSecretKey AWSシークレットキー
	 */
	public static void saveAwsSecretKey(final String awsSecretKey) {
		AWS_SECRET_KEY.set(awsSecretKey);
		AWS_SECRET_KEY.save();
	}

	/**
	 * GoogleAIのAPIキー保存処理
	 * 
	 * @param key GoogleAIのAPIキー
	 */
	public static void saveGoogleAIKey(final String key) {
		GOOGLEAI_API_KEY.set(key);
		GOOGLEAI_API_KEY.save();
	}

	/**
	 * モブ毎のメッセージ保存処理
	 * 
	 * @param mobName モブ名
	 * @param message メッセージ
	 */
	public static void saveChatMessage(final String mobName, final String message) {
		chatMessages.put(mobName, message);

		List<String> chatMessageList = new ArrayList<String>();
		chatMessages.forEach((key, value) -> chatMessageList.add(key + "@@" + value));

		CHAT_MESSAGES.set(chatMessageList);
		CHAT_MESSAGES.save();
	}

	/**
	 * モブ毎のメッセージクリア
	 */
	public static void resetChatMessage() {
		CHAT_MESSAGES.set(Arrays.asList("default@@default"));
		CHAT_MESSAGES.save();
	}
}
