data class CustomFilter(
    val type: Set<String> = emptySet(),
    val day: Set<String> = emptySet(),
    val restrictions: Set<String> = emptySet(),
    val minPrice: Float = 0f,
    val maxPrice: Float = 100f
)