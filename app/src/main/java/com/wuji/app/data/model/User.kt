package com.wuji.app.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class PaymentStatus { PENDING, COMPLETED, FAILED, REFUNDED }

@Serializable
enum class MembershipPlanLevel { BASIC, VIP, PRO }

@Serializable
enum class MembershipPlanBillingCycle { MONTHLY, QUARTERLY, YEARLY, LIFETIME }

@Serializable
data class MembershipPlan(
    val id: String = "",
    val name: String = "",
    val level: MembershipPlanLevel = MembershipPlanLevel.BASIC,
    val price: Double = 0.0,
    val originalPrice: Double = 0.0,
    val billingCycle: MembershipPlanBillingCycle = MembershipPlanBillingCycle.MONTHLY,
    val features: List<String> = emptyList(),
    val discount: String = "",
)

@Serializable
data class MembershipOrder(
    val id: String = "",
    val userId: String = "",
    val plan: MembershipPlan = MembershipPlan(),
    val amount: Double = 0.0,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val paymentUrl: String = "",
    val createdAt: Long = 0L,
    val expiresAt: Long = 0L,
) {
    fun isValid(): Boolean {
        return paymentStatus == PaymentStatus.COMPLETED && expiresAt > System.currentTimeMillis()
    }
}

@Serializable
data class UserInfo(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val photo: String = "",
    val phone: String = "",
    val isVerified: Boolean = false,
    val memberships: List<MembershipOrder> = emptyList(),
    val token: String = "",
) {
    val isVip: Boolean
        get() = memberships.any { it.isValid() && (it.plan.level == MembershipPlanLevel.VIP || it.plan.level == MembershipPlanLevel.PRO) }
    val isPro: Boolean
        get() = memberships.any { it.isValid() && it.plan.level == MembershipPlanLevel.PRO }
}

@Serializable
data class Feature(
    val key: String = "",
    val label: String = "",
    val description: String = "",
    val enableVip: Boolean = false,
    val enablePro: Boolean = false,
)
