package com.example.playlistmaker.media.presentation.mapper

import com.example.playlistmaker.media.models.ParcelablePlaylist
import com.example.playlistmaker.media.models.Playlist


object ParcelablePlaylistMapper {

    fun map(playlist: Playlist): ParcelablePlaylist {
        return ParcelablePlaylist(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            filePath = playlist.filePath,
            trackList = playlist.trackList as ArrayList<Long>,
            trackCount = playlist.trackCount
        )
    }

    fun map(parcelablePlaylist: ParcelablePlaylist): Playlist {
        return Playlist(
            id = parcelablePlaylist.id,
            name = parcelablePlaylist.name,
            description = parcelablePlaylist.description,
            filePath = parcelablePlaylist.filePath,
            trackList = parcelablePlaylist.trackList,
            trackCount = parcelablePlaylist.trackCount
        )
    }
}