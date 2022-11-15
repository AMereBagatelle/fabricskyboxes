package io.github.amerebagatelle.fabricskyboxes.util.`object`

enum class Weather(
    val weather: String?
) {
    CLEAR("clear"),
    RAIN("rain"),
    SNOW("snow"),
    THUNDER("thunder");

    companion object {
        fun from(value: String): Weather? = Weather.values().firstOrNull { it.weather == value}
    }
}
