package com.example.iosdev.sensorproject

import java.util.HashSet

/**
 * Created by iosdev on 27.9.2016.
 */

class DiscountCreator {

    var discounts: Set<String> = HashSet(20)

    fun makeDiscounts() {
        val d1 = "1km - Free coffee from the Metropolia Unicafe - MUC123"
        discounts.add(d1)

        val d2 = "2km - 50% off from Luhta winter jackets from the Intersport - IL912"
        discounts.add(d2)

        val d3 = "3km - 25% off from any food you order in Amarillo - A3139"
        discounts.add(d3)

        val d4 = "4km - Helsingin Sanomat for 6 months only 20,00€ - HS0900"
        discounts.add(d4)

        val d5 = "4km - Mens haircut only 12€ in Style Workshop Kruununhaka - SWK2922"
        discounts.add(d5)

        val d6 = "2km - Free car wash in Koskelan Autopesu - KAP8889"
        discounts.add(d6)

        val d7 = "1km - Chefs menu 10€ in Töölön Sävel - TS1231"
        discounts.add(d7)

        val d8 = "3km - Free Gym membership in Fitness 24/7 - F1223"
        discounts.add(d8)

        val d9 = "4km - Exit room game for 1-6 people only 9€ in Exit Room Helsinki - ER5582"
        discounts.add(d9)

        val d10 = "4km - Free bucket from Tokmanni - FB9942"
        discounts.add(d10)


    }
}
