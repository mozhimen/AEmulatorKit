package com.mozhimen.emulatork.test.shared

import android.view.ContextMenu
import android.view.View
import com.mozhimen.emulatork.test.R

/**
 * @ClassName GameContextMenuListener
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/10
 * @Version 1.0
 */
class GameContextMenuListener(
    private val isFavorite: Boolean?,
    private val onPlaySelected: (() -> Unit)?,
    private val onRestartSelected: (() -> Unit)?,
    private val onFavoriteChanged: ((Boolean) -> Unit)?
) : View.OnCreateContextMenuListener {

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        menu.add(R.string.play).setOnMenuItemClickListener {
            onPlaySelected?.invoke()
            true
        }

        menu.add(R.string.restart).setOnMenuItemClickListener {
            onRestartSelected?.invoke()
            true
        }

        when (isFavorite) {
            true -> {
                menu.add(R.string.remove_from_favorites).setOnMenuItemClickListener {
                    onFavoriteChanged?.invoke(false)
                    true
                }
            }
            false -> {
                menu.add(R.string.add_to_favorites).setOnMenuItemClickListener {
                    onFavoriteChanged?.invoke(true)
                    true
                }
            }
            else -> Unit // Do nothing if is favorite is null
        }
    }
}
