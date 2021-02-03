package dev.kallytang.chompalpha.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Item(
    var name: String? = "",
    var brand: String? = "",
    var quantity: Int = 1,
    var units: Unit? = null,
    @get:PropertyName("expiry_date") @set:PropertyName("expiry_date") var expiryDate: Timestamp?= null,
    var location: String? = "",
    var notes: String? = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String? = "",
    @Exclude @DocumentId val documentId: String? = "")
:Parcelable





// future fields:

//    @get:PropertyName("in_pantry") @set:PropertyName("in_pantry")var inPantry: Boolean = true,
//    @get:PropertyName("quantity_to_buy") @set:PropertyName("quantity_to_buy")var quantityToBuy: Number = 1,
//    @get:PropertyName("is_grocery_item") @set:PropertyName("is_grocery_item")var isGroceryItem: Boolean = false,

//    @DocumentId @Exclude var documentId: DocumentId