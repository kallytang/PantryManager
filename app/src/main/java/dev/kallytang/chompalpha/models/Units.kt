package dev.kallytang.chompalpha.models

import com.google.firebase.firestore.PropertyName

data class Units(
    @get: PropertyName("unit_name") @set: PropertyName("unit_name") var unitName: String = "",
    var abbreviation: String = ""
)

