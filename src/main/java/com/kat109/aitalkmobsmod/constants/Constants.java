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
	public static final String ANTHROPIC_MODEL_CLAUDE2 = "anthropic.claude-v2";
	public static final String ANTHROPIC_MODEL_CLAUDE2_1 = "anthropic.claude-v2:1";
	public static final String ANTHROPIC_MODEL_GEMINI_PRO = "gemini-pro";

	// OpenAIのモデル
	public static final String[] OPENAI_MODELS = {
			OPENAI_MODEL_GPT3_5_TURBO,
			OPENAI_MODEL_GPT4,
			OPENAI_MODEL_GPT4_TURBO_PREVIEW
	};

	// Anthropicのモデル
	public static final String[] ANTHROPIC_MODELS = {
			ANTHROPIC_MODEL_CLAUDE2,
			ANTHROPIC_MODEL_CLAUDE2_1
	};

	// GoogleAIのモデル
	public static final String[] GOOGLEAI_MODELS = {
			ANTHROPIC_MODEL_GEMINI_PRO
	};
}
