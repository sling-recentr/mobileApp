package com.slingHealth.reCentr


import java.util.*

object AdafruitBluefruitLeAttributes {

    private val attributes = HashMap<String, String>()
    //   public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    var CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb"

    // private static final String DEVICE_NAME = "Adafruit Bluefruit LE";

    // UUIDs for UART service and associated characteristics.
    val UART_UUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
    val RX_UUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"

    // UUID for the UART BTLE client characteristic which is necessary for notifications.
    // public static String CLIENT_UUID = "00002902-0000-1000-8000-00805f9b34fb";
    // UUIDs for the Device Information service and associated characeristics.
    // public static String DIS_MANUF_UUID = "00002a29-0000-1000-8000-00805f9b34fb";
    // public static String DIS_MODEL_UUID = "00002a24-0000-1000-8000-00805f9b34fb";


    init {
        // Sample Services.
        attributes[UART_UUID] = "UART"
        // Sample Characteristics.
        attributes[RX_UUID] = "RX Characteristic"
        attributes["00002a29-0000-1000-8000-00805f9b34fb"] = "Manufacturer Name String"
    }

    fun lookup(uuid: String, defaultName: String): String {
        val name = attributes.get(uuid)
        return if (name == null) defaultName else name
    }
}
