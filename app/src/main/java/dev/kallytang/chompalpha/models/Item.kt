package dev.kallytang.chompalpha.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.lang.NumberFormatException
import java.sql.Timestamp

data class Item(
    @DocumentId @Exclude var documentId: DocumentId,
    var name: String = "",
    var units: Units? = null,
    var quantity: Number = 1,
    @get:PropertyName("expiry_date") @set:PropertyName("expiry_date")var expiryDate: Timestamp? = null,
    var brand: String = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String = "",
    var location: String = "",
    var notes: String = "",
    @get:PropertyName("in_pantry") @set:PropertyName("in_pantry")var inPantry: Boolean = true,
    @get:PropertyName("quantity_to_buy") @set:PropertyName("quantity_to_buy")var quantityToBuy: Number = 1,
    @get:PropertyName("is_grocery_item") @set:PropertyName("is_grocery_item")var isGroceryItem: Boolean = false

)