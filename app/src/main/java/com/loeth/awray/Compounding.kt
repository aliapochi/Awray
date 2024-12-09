package com.loeth.awray

fun main() {
    print("Enter your initial principal: ")
    val principal = readLine()?.toDoubleOrNull() ?: 0.0 // Safely parse input
    print("Enter your initial principal: ")
    val dailyInterestRate = readLine()?.toDoubleOrNull() ?: 0.0 // 6.6% interest rate
    val withdrawalPeriod = 20 // Every 20 days
    val totalDays = 240 // Total duration

    var currentPrincipal = principal
    var totalWithdrawn = 0.0

    for (day in 1..totalDays) {
        // Calculate daily interest and update principal
        val dailyInterest = currentPrincipal * dailyInterestRate
        currentPrincipal += dailyInterest

        // Perform withdrawal every 20 days
        if (day % withdrawalPeriod == 0) {
            val withdrawalAmount = currentPrincipal * 0.5 // 50% of total amount
            totalWithdrawn += withdrawalAmount
            currentPrincipal -= withdrawalAmount // Update remaining principal
            println("Day $day: Withdrawn $withdrawalAmount, Remaining principal $currentPrincipal")
        }
    }

    println("At the end of $totalDays days:")
    println("Total Withdrawn: $totalWithdrawn")
    println("Remaining Principal: $currentPrincipal")
}
