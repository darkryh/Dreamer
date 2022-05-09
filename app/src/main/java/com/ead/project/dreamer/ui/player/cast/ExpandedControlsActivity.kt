package com.ead.project.dreamer.ui.player.cast

import android.os.Bundle
import android.view.Menu
import androidx.activity.viewModels
import com.ead.project.dreamer.R
import com.ead.project.dreamer.data.utils.media.CastManager
import com.ead.project.dreamer.data.utils.ui.DreamerUIController
import com.ead.project.dreamer.ui.player.PlayerViewModel
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExpandedControlsActivity  : ExpandedControllerActivity() {

    private var castManager : CastManager = CastManager(true)
    private val playerViewModel : PlayerViewModel by viewModels()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val customButtonView = getButtonImageViewAt(0)
        val dreamerUIController = DreamerUIController(customButtonView)
        uiMediaController.bindViewToUIController(customButtonView, dreamerUIController)
        castManager.setViewModel(playerViewModel)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.expanded_controller, menu)
        CastButtonFactory.setUpMediaRouteButton(this, menu!!, R.id.media_route_menu_item)
        return true
    }

    override fun onPause() {
        castManager.updateChapterMetaData()
        super.onPause()
    }

    override fun onDestroy() {
        castManager.onDestroy()
        super.onDestroy()
    }
}