package com.ead.project.dreamer.domain.servers

import com.ead.project.dreamer.data.commons.Constants
import javax.inject.Inject

class GetServerResultToArray @Inject constructor() {

    operator fun invoke(string: String) : List<String> =
        string
            .removePrefix("[\"")
            .removeSuffix("\"]")
            .split("${Constants.QUOTATION},${Constants.QUOTATION}")
}