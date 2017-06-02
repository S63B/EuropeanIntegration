package nl.s63b.europeanintegration.jms.kotlin

import nl.s63b.europeanintegration.jms.kotlin.Countries

/**
 * Created by guushamm on 23-5-17.
 */
 data class Car(val licensePlate: String, val countryOfOrigin: Countries, val stolen: Boolean)