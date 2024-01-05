package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.db.ExternalNavigatorMedia
import com.example.playlistmaker.media.domain.db.PlaylistInteractor
import com.example.playlistmaker.media.domain.db.PlaylistRepository
import com.example.playlistmaker.media.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.presentation.utils.toastTrackText
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository,
    private val externalNavigatorMedia: ExternalNavigatorMedia
) :
    PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist): Long {
        return playlistRepository.addPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override suspend fun getPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getFlowPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlistId: Long) {
        playlistRepository.addTrackToPlaylist(track, playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(track: Track, playlistId: Long) {
        playlistRepository.deleteTrackFromPlaylist(track, playlistId)
    }

    override suspend fun getPlaylistById(id: Long): Flow<Playlist?> {
        return playlistRepository.getFlowPlaylistById(id)
    }

    override suspend fun getPlaylistTracksByTrackIdList(trackIdList: List<Long>): List<Track> {
        return playlistRepository.getPlaylistTracksByTrackIdList(trackIdList)
    }

    override fun getPlaylistInfo(playlist: Playlist, trackList: List<Track>): String {
        val tracks = StringBuilder()
        trackList.forEachIndexed { index, track ->
            tracks.append("\n" + index + 1 + "." + track.artistName + " - " + track.collectionName
                    + "(${SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis?.toInt())})"
            )
        }

        return playlist.name + "\n" + (playlist.description ?: "") + "\n" + toastTrackText(
            playlist.trackCount
        ) + tracks.toString()
    }

    override fun sharePlaylist(playlistInfo: String) {
        externalNavigatorMedia.sharePlaylist(playlistInfo)
    }
}
