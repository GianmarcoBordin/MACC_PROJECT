package com.mygdx.game.domain.usecase.map

import com.mygdx.game.data.dao.Player
import com.mygdx.game.domain.manager.RankManager


class UpdateUserLocation(
    private val rankManager: RankManager
) {
    suspend operator fun invoke(player: Player) {
        rankManager.updateUserLocation(player)
    }

}