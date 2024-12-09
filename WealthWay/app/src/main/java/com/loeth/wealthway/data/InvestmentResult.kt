package com.loeth.wealthway.data

data class InvestmentResult(
    val day: Int,
    val principal: Double,
    val interestEarned: Double,
    val totalWithdrawn: Double,
    val remainingBalance: Double
)

