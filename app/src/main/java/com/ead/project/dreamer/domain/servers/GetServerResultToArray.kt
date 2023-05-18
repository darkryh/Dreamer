package com.ead.project.dreamer.domain.servers

import javax.inject.Inject

class GetServerResultToArray @Inject constructor() {

    operator fun invoke(string: String) : List<String> =
        string
            .removePrefix("[\"")
            .removeSuffix("\"]")
            .split("\",\"")
}