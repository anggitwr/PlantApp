package com.anggitwr.myplantapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tanaman (
        var namaDaun : String,
        var namaLatin : String,
        var Images : Int,
        var manfaatDaun1 : String,
        var manfaatDaun2 : String
        ) : Parcelable