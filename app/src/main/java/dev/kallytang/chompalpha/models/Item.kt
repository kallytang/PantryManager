package dev.kallytang.chompalpha.models

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.PARCELABLE_WRITE_RETURN_VALUE
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize

@Parcelize
@IgnoreExtraProperties
data class Item(
    var name: String? = "",
    var brand: String? = "",
    var quantity: Int = 1,
    var unit: Unit? = null,
    @get:PropertyName("expiry_date") @set:PropertyName("expiry_date") var expiryDate: com.google.firebase.Timestamp?= null,
    var location: String? = "",
    var notes: String? = "",
    @get:PropertyName("image_url") @set:PropertyName("image_url") var imageUrl: String? = "",
    @Exclude @DocumentId val documentId: String? = "",

    ) : Parcelable
{

    private constructor(parcelIn: Parcel):this(
        name = parcelIn.readString(),
        brand = parcelIn.readString(),
        quantity = parcelIn.readInt(),
        unit = parcelIn.readParcelable<Unit>(Unit::class.java.classLoader),
        expiryDate = parcelIn.readSerializable() as Timestamp,
        location = parcelIn.readString(),
        notes = parcelIn.readString(),
        imageUrl = parcelIn.readString(),
        documentId = parcelIn.readString()
    )


    private companion object : Parceler<Item> {

        override fun Item.write(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
            parcel.writeString(brand)
            parcel.writeInt(quantity)
            parcel.writeParcelable(unit, PARCELABLE_WRITE_RETURN_VALUE)
            parcel.writeValue(expiryDate)
            parcel.writeString(location)
            parcel.writeString(notes)
            parcel.writeString(imageUrl)
            parcel.writeString(documentId)
        }

        override fun create(parcel: Parcel): Item {
            return Item(parcel)
        }
    }
}



// future fields:

//    @get:PropertyName("in_pantry") @set:PropertyName("in_pantry")var inPantry: Boolean = true,
//    @get:PropertyName("quantity_to_buy") @set:PropertyName("quantity_to_buy")var quantityToBuy: Number = 1,
//    @get:PropertyName("is_grocery_item") @set:PropertyName("is_grocery_item")var isGroceryItem: Boolean = false,

//    @DocumentId @Exclude var documentId: DocumentId