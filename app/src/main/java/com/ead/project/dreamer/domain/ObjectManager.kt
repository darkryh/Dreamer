package com.ead.project.dreamer.domain

import com.ead.project.dreamer.domain.operations.DeleteObject
import com.ead.project.dreamer.domain.operations.InsertObject
import com.ead.project.dreamer.domain.operations.UpdateObject
import javax.inject.Inject

class ObjectManager @Inject constructor(
    val insertObject: InsertObject,
    val updateObject: UpdateObject,
    val deleteObject: DeleteObject
)