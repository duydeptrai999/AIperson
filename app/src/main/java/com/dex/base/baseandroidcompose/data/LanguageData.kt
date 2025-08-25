package com.dex.base.baseandroidcompose.data

import com.dex.base.baseandroidcompose.R

enum class LanguageData(val displayName: String, val languageCode: String, val flagIconId: Int) {
    ENGLISH("English", "en", R.drawable.icon_flag_england),
    PORTUGUESE("Português (Brasil)", "pt-BR", R.drawable.icon_flag_portugal), 
    JAPANESE("日本語", "ja", R.drawable.icon_flag_japan),
    KOREAN("한국어", "ko", R.drawable.icon_flag_south_korea),
    GERMAN("Deutsch", "de", R.drawable.icon_flag_germany),
    FRENCH("Français", "fr", R.drawable.icon_flag_france), 
    HINDI("हिन्दी", "hi", R.drawable.icon_flag_india),
    ITALIAN("Italiano", "it", R.drawable.icon_flag_italy),
    VIETNAMESE("Tiếng Việt", "vi", R.drawable.icon_flag_vietnam),
}