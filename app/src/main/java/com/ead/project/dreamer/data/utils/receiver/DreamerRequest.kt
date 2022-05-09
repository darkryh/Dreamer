package com.ead.project.dreamer.data.utils.receiver



import kotlin.random.Random

class DreamerRequest {

    companion object {

        fun getExampleLoad() = getRandomLoad()

        private fun getRandomLoad(): String =
            exampleLoads[Random.nextInt(exampleLoads.size - 1)]

        fun userAgent(): String = randomUserAgent()

        fun getSpecificUserAgent(pos: Int): String = try {
            userAgentList[pos]
        } catch (e: Exception) {
            userAgentList[0]
        }

        private fun randomUserAgent(): String =
            userAgentList[Random.nextInt(userAgentList.size - 1)]

        const val USER_AGENT = "User-agent"

        private val userAgentList = listOf(
            "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.99 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.54 Safari/537.36",
            "Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/95.0.4638.50 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (iPad; CPU OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/95.0.4638.50 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (iPod; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/95.0.4638.50 Mobile/15E148 Safari/604.1",
            "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.50 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; SM-A205U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.50 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; SM-A102U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.50 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; SM-G960U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.50 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; SM-N960U) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.50 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; LM-Q720) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.50 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; LM-X420) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.50 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; LM-Q710(FGN)) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/95.0.4638.50 Mobile Safari/537.36"
        )

        private val exampleLoads = listOf(
            "https://monoschinos2.com/anime/evangelion-222-you-can-not-advance-latino-sub-espanol",
            "https://monoschinos2.com/anime/bubble-sub-espanol",
            "https://monoschinos2.com/anime/mind-game-sub-espanol",
            "https://monoschinos2.com/anime/ryuusei-no-rockman-tribe-megaman-star-force-tribe-sub-espanol",
            "https://monoschinos2.com/anime/ladyspo-sub-espanol",
            "https://monoschinos2.com/anime/senki-zesshou-symphogear-sub-espanol",
            "https://monoschinos2.com/anime/petit-eva-evangelion-at-school-sub-espanol",
            "https://monoschinos2.com/anime/sorairo-utility-sub-espanol",
            "https://monoschinos2.com/anime/yuuyuuhakusho-special-sub-espanol",
            "https://monoschinos2.com/anime/ore-ga-ojousama-gakkou-ni-shomin-sample-toshite-gets-sareta-ken-especiales-sub-espanol",
            "https://monoschinos2.com/anime/monster-strike-the-movie-lucifer-zetsubou-no-yoake-sub-espanol",
            "https://monoschinos2.com/anime/nanatsu-no-bitoku-sub-espanol",
            "https://monoschinos2.com/anime/love-hina-spring-especial-castellano-sub-espanol",
            "https://monoschinos2.com/anime/karin-sub-espanol",
            "https://monoschinos2.com/anime/watashi-ni-tenshi-ga-maiorita-sub-espanol",
            "https://monoschinos2.com/anime/tribe-nine-sub-espanol",
            "https://monoschinos2.com/anime/maken-ki-two-sub-espanol",
            "https://monoschinos2.com/anime/koe-de-oshigoto-sub-espanol",
            "https://monoschinos2.com/anime/rezero-kara-hajimeru-break-time-2nd-season-sub-espanol",
            "https://monoschinos2.com/anime/the-idolmaster-movie-kagayaki-no-mukougawa-e-sub-espanol",
            "https://monoschinos2.com/anime/city-hunter-sub-espanol",
            "https://monoschinos2.com/anime/yuuki-yuuna-wa-yuusha-de-aru-churutto-sub-espanol",
            "https://monoschinos2.com/anime/log-horizon-2-temporada-sub-espanol",
            "https://monoschinos2.com/anime/servamp-sub-espanol",
            "https://monoschinos2.com/anime/kowarekake-no-orgel-especial-sub-espanol",
            "https://monoschinos2.com/anime/hiatari-ryoukou-alegre-juventud-castellano-sub-espanol",
            "https://monoschinos2.com/anime/densetsu-no-yuusha-no-densetsu-sub-espanol",
            "https://monoschinos2.com/anime/alien-9-sub-espanol",
            "https://monoschinos2.com/anime/juliet-sub-espanol",
            "https://monoschinos2.com/anime/ooyasan-wa-shishunki-sub-espanol",
            "https://monoschinos2.com/anime/digimon-xros-wars-latino-sub-espanol",
            "https://monoschinos2.com/anime/kitsune-no-koe-sub-espanol",
            "https://monoschinos2.com/anime/wz-sub-espanol",
            "https://monoschinos2.com/anime/usagi-drop-especiales-sub-espanol",
            "https://monoschinos2.com/anime/tamako-market-minami-no-shima-no-dera-chan-sub-espanol",
            "https://monoschinos2.com/anime/lum-la-chica-invasora-urusei-yatsura-castellano-sub-espanol",
            "https://monoschinos2.com/anime/majutsushi-orphen-revenge-orphen-revenge-castellano-sub-espanol",
            "https://monoschinos2.com/anime/squishy-black-clover-sub-espanol",
            "https://monoschinos2.com/anime/kaibutsu-oujo-sub-espanol",
            "https://monoschinos2.com/anime/serial-experiments-lain-latino-sub-espanol",
            "https://monoschinos2.com/anime/super-crooks-sub-espanol",
            "https://monoschinos2.com/anime/gakuen-utopia-manabi-straight-sub-espanol",
            "https://monoschinos2.com/anime/gatchaman-crowds-insight-sub-espanol",
            "https://monoschinos2.com/anime/mushishi-zoku-shou-suzu-no-shizuku-sub-espanol",
            "https://monoschinos2.com/anime/fatekaleid-liner-prismaillya-3rei-especiales-sub-espanol",
            "https://monoschinos2.com/anime/yarichin-bitch-bu-1080p-sub-espanol",
            "https://monoschinos2.com/anime/macross-plus-sub-espanol",
            "https://monoschinos2.com/anime/mobile-suit-gundam-0080-war-in-the-pocket-sub-espanol",
            "https://monoschinos2.com/anime/k-on-2-ura-on-2-sub-espanol",
            "https://monoschinos2.com/anime/relife-sub-espanol"
        )
    }
}