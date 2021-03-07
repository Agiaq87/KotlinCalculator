package com.giaquinto.kalc


// UNUSED
class IntegerAlu {

    private var firstDigit: Boolean = true

    internal var value: Int = 0
        get() = field
        set(value) {
            if (firstDigit) {
                firstDigit = false
                field = value
            } else {
                field *= 10
                field += value
            }
        }

    var handle: Int = 0
        get() { return this.handle }

}