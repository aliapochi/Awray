package com.loeth.wealthway.logic

import com.loeth.wealthway.data.InvestmentResult

class InvestmentCalculator {
    fun calculateEarnings(
        principal: Double,
        dailyRate: Double = 0.066,
        withdrawalPercentage: Double = 0.5,
        withdrawalCycle: Int = 20,
        totalDays: Int = 240
    ): List<InvestmentResult> {
        val results = mutableListOf<InvestmentResult>()
        var currentPrincipal = principal

        for (day in 1..totalDays) {
            val dailyInterest = currentPrincipal * dailyRate
            currentPrincipal += dailyInterest

            if (day % withdrawalCycle == 0) {
                val withdrawalAmount = currentPrincipal * withdrawalPercentage
                currentPrincipal -= withdrawalAmount
                results.add(
                    InvestmentResult(
                        day = day,
                        principal = currentPrincipal,
                        interestEarned = dailyInterest,
                        totalWithdrawn = withdrawalAmount,
                        remainingBalance = currentPrincipal
                    )
                )
            }
        }
        return results
    }
}
