package dev.kallytang.chompalpha.models

import com.google.firebase.firestore.Exclude
import java.lang.NumberFormatException
import java.sql.Timestamp

data class Item(
    @Exclude var item_id: String? =null,
    var name: String = "",
    var units: String = "",
    var quantity: Number = 1,
    var expiry_date: Timestamp? = null,
    var brand: String = "",
    var image_url: String = "",
    var location: String = "",
    var notes: String = "",
    var in_pantry: Boolean = true,
    var quantity_to_buy: Number = 1,
    var is_grocery_item: Boolean = false
)