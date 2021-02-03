package dev.kallytang.chompalpha.models

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import com.bumptech.glide.util.Util
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import java.text.ParseException


@Parcelize
data class Unit(
    @get: PropertyName("unit_name") @set: PropertyName("unit_name") var unitName: String? = "",
    var abbreviation: String? = ""
): Parcelable
{
    private constructor(parcelIn: Parcel):this(
        unitName = parcelIn.readString(),
        abbreviation = parcelIn.readString()

    )


    companion object : Parceler<Unit> {

        override fun Unit.write(parcel: Parcel, flags: Int) {
            parcel.writeString(unitName)
            parcel.writeString(abbreviation)
        }

        override fun create(parcel: Parcel): Unit {
           return Unit(parcel)
        }
    }


}

