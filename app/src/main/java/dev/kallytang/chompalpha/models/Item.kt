package dev.kallytang.chompalpha.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import java.lang.NumberFormatException
import java.sql.Timestamp

data class Item(

    var name: String = "",
    var brand: String = "",
    var quantity: Number = 1,
    var units: Units? = null,
    @get:PropertyName("expiry_date") @set:PropertyName("expiry_date")var expiryDate: com.google.firebase.Timestamp? = null,
    var location: String = "",
    var notes: String = "",


)

// future fields:
//    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String = "",

//    @get:PropertyName("in_pantry") @set:PropertyName("in_pantry")var inPantry: Boolean = true,
//    @get:PropertyName("quantity_to_buy") @set:PropertyName("quantity_to_buy")var quantityToBuy: Number = 1,
//    @get:PropertyName("is_grocery_item") @set:PropertyName("is_grocery_item")var isGroceryItem: Boolean = false,

//    @DocumentId @Exclude var documentId: DocumentId