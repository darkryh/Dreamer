package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.apis.discord.*
import javax.inject.Inject

class DiscordUseCase @Inject constructor(
    val getDiscordMember: GetDiscordMember
)