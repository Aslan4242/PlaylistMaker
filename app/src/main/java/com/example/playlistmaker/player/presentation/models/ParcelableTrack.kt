package com.example.playlistmaker.player.presentation.models

import android.os.Parcel
import android.os.Parcelable
import java.util.Date

data class ParcelableTrack(
    val trackName: String?,
    val artistName: String?,
    val trackTime: Long?,
    val artworkUrl100: String?,
    val albumName: String?,
    val releaseDate: String?,
    val genreName: String?,
    val country: String?,
    val previewUrl: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    constructor() : this(null, null, null, null, null, null, null, null, null)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(trackName)
        parcel.writeString(artistName)
        parcel.writeLong(trackTime ?: 0)
        parcel.writeString(artworkUrl100)
        parcel.writeString(albumName)
        parcel.writeString(releaseDate)
        parcel.writeString(genreName)
        parcel.writeString(country)
        parcel.writeString(previewUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelableTrack> {
        override fun createFromParcel(parcel: Parcel): ParcelableTrack {
            return ParcelableTrack(parcel)
        }

        override fun newArray(size: Int): Array<ParcelableTrack?> {
            return arrayOfNulls(size)
        }
    }
}
