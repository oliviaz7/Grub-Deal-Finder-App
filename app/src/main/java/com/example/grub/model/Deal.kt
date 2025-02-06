package com.example.grub.model


data class Deal(
    val id: String,
    val item: String,
    val description: String? = null,
    val type: DealType,
    val placeId: String, // TODO: is this actually a string?
    val restaurantName: String, // necessary?
//    val expiryDate: Date, // TOO LAZY TO FIGURE THIS OUT RN I'LL DO IT LATER
//    val datePosted: List<Paragraph> = emptyList(),
    val userId: String,
    val restrictions: String, // TODO: make this not a string lol
)

enum class DealType {
    BOGO,
    DISCOUNT,
    // TODO: add more
}
