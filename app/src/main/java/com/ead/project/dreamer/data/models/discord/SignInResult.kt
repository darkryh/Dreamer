package com.ead.project.dreamer.data.models.discord

import com.ead.project.dreamer.app.model.EadAccount

class SignInResult(
    val data : EadAccount?,
    val errorMessage : String?
)