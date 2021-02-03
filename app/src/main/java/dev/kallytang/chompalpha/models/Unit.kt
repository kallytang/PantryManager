package dev.kallytang.chompalpha.models

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Unit(
    @get: PropertyName("unit_name") @set: PropertyName("unit_name") var unitName: String? = "",
    var abbreviation: String? = ""
) : Parcelable
