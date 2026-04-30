package org.tasks.billing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class LocalSubscriptionProvider : SubscriptionProvider {
    override val subscription: Flow<SubscriptionProvider.SubscriptionInfo?> =
        MutableStateFlow(
            SubscriptionProvider.SubscriptionInfo(
                sku = "local_pro",
                isMonthly = false,
                isTasksSubscription = false,
            )
        )

    override suspend fun getFormattedPrice(sku: String): String? = null
}
