package com.brilliancesoft.xplayer.ui.user_activity.playlist


import android.os.Bundle
import android.view.MenuItem
import com.brilliancesoft.xplayer.R
import com.brilliancesoft.xplayer.model.Playlist
import com.brilliancesoft.xplayer.ui.commen.media.BaseMediaFragment
import kotlinx.android.synthetic.main.fragment_media_playlist.*
import kotlinx.android.synthetic.main.toolbar_details.*
import kotlinx.serialization.json.Json


class MediaPlaylistFragment : BaseMediaFragment(false) {


    override val playlist: Playlist by lazy {
        Json.parse(
            Playlist.serializer(),
            MediaPlaylistFragmentArgs.fromBundle(requireArguments()).playlistMediaListArg)

    }
    override val layoutId: Int = R.layout.fragment_media_playlist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated() {

        detailsToolbar.title = playlist.name

        detailsToolbar.inflateMenu(R.menu.menu_media_list)
        mediaAdapter.isAddedToPlaylist = true
        mediaPlaylistRecycler.adapter = mediaAdapter
        mediaAdapter.updateList(playlist.list)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.deletePlayList) deletePlaylist()
        return true
    }
}


