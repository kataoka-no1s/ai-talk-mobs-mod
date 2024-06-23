package com.kat109.aitalkmobsmod.constants;

/**
 * 定数定義ファイル
 */
public class Constants {
	// MODのID
	public static final String MODID = "aitalkmobsmod";

	// AIモデル名（APIリクエスト時のモデル名）
	public static final String OPENAI_MODEL_GPT3_5_TURBO = "gpt-3.5-turbo";
	public static final String OPENAI_MODEL_GPT4 = "gpt-4";
	public static final String OPENAI_MODEL_GPT4_TURBO_PREVIEW = "gpt-4-turbo-preview";
	public static final String OPENAI_MODEL_GPT4_O = "gpt-4o";
	public static final String ANTHROPIC_MODEL_CLAUDE2 = "claude-2.0";
	public static final String ANTHROPIC_MODEL_CLAUDE2_1 = "claude-2.1";
	public static final String ANTHROPIC_MODEL_CLAUDE3_OPUS = "claude-3-opus-20240229";
	public static final String ANTHROPIC_MODEL_CLAUDE3_SONNET = "claude-3-sonnet-20240229";
	public static final String ANTHROPIC_MODEL_CLAUDE3_HAIKU = "claude-3-haiku-20240307";
	public static final String AWS_BEDROCK_MODEL_CLAUDE2 = "anthropic.claude-v2";
	public static final String AWS_BEDROCK_MODEL_CLAUDE2_1 = "anthropic.claude-v2:1";
	public static final String GOOGLE_AI_MODEL_GEMINI_PRO = "gemini-pro";

	// OpenAIのモデル
	public static final String[] OPENAI_MODELS = {
			OPENAI_MODEL_GPT3_5_TURBO,
			OPENAI_MODEL_GPT4,
			OPENAI_MODEL_GPT4_TURBO_PREVIEW,
			OPENAI_MODEL_GPT4_O
	};

	// Anthropicのモデル
	public static final String[] ANTHROPIC_MODELS = {
			ANTHROPIC_MODEL_CLAUDE2,
			ANTHROPIC_MODEL_CLAUDE2_1,
			ANTHROPIC_MODEL_CLAUDE3_OPUS,
			ANTHROPIC_MODEL_CLAUDE3_SONNET,
			ANTHROPIC_MODEL_CLAUDE3_HAIKU
	};

	// GoogleAIのモデル
	public static final String[] GOOGLEAI_MODELS = {
			GOOGLE_AI_MODEL_GEMINI_PRO
	};

	// AwsBedrockのモデル
	public static final String[] AWS_BEDROCK_MODELS = {
			AWS_BEDROCK_MODEL_CLAUDE2,
			AWS_BEDROCK_MODEL_CLAUDE2_1
	};
}
