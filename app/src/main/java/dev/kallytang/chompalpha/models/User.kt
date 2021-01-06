package dev.kallytang.chompalpha.models

data class User(
    var username: String = "",
    var first_name: String = "",
    var image_url: String ="",
    var limited_plan: Boolean = true,
    var pantry_ref: String = ""

)