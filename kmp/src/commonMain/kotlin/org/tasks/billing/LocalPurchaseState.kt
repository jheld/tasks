package org.tasks.billing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class LocalPurchaseState : PurchaseState {
    override val hasTasksAccount: Boolean
        get() = true
    override val hasPro: Boolean
        get() = true
    override val hasTasksSubscription: Boolean
        get() = true
}
