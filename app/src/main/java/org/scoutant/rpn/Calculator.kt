package org.scoutant.rpn

import android.text.method.TextKeyListener.clear
import org.scoutant.rpn.BigDecimalUtils.approxPow
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

class Calculator () {
    val stack: Stack<BigDecimal> = Stack()
    private val PRECISION = 10
    private val INTERNAL_SCALE = 32

    constructor (cache: String) : this() {
        for (item in cache.split(";")) {
            if (item.isEmpty()) continue
            stack.push( BigDecimal( item))
        }
    }

    fun push( value: BigDecimal) {
        stack.push( value)
    }

    fun stack(): Stack<BigDecimal>? {
        if (stack.isEmpty()) return null
        return stack
    }

    fun push( value: String) = push ( BigDecimal( value))

    fun push( value: Int) = push ( BigDecimal( value))
    fun push( value: Double) = push ( BigDecimal( value))

    fun isEmpty(): Boolean = stack.isEmpty()

    fun peek() : BigDecimal = stack.peek()
    fun pop() : BigDecimal = stack.pop()

    fun drop() = stack()?.pop()

    fun dup() = stack()?.push( peek())

    fun size(): Int = stack.size
    fun small(): Boolean = size() <= 1

    fun swap() {
        if ( small()) return
        val x = pop()
        val y = pop()
        push( x)
        push( y)
    }

    fun add() {
        if (small()) return
        push(pop().add( pop()) )
    }

    fun subtract() {
        if (small()) return
        val x = pop()
        push( pop().subtract( x))
    }

    fun multiply() {
        if (small()) return
        val x = pop()
        push( pop().multiply( x))
    }

    fun divide() {
        if (small()) return
        if (BigDecimal.ZERO == peek()) throw ArithmeticException("UNDEFINED")
        val x = pop()
        push( pop().divide( x, INTERNAL_SCALE, RoundingMode.HALF_EVEN))
    }

    fun power() {
        if (stack.isEmpty()) return
        val num = pop()
        push(num * num)
    }

    fun sqrt() {
        if (stack.isEmpty()) return
        if (peek().toDouble()<0) throw ArithmeticException("UNDEFINED")
        push( BigDecimalUtils.sqrt( pop(), INTERNAL_SCALE))
    }

    fun reciprocal() {
        if (stack.isEmpty()) return
        if (BigDecimal.ZERO == peek()) throw ArithmeticException("UNDEFINED")
        push( BigDecimal.ONE.divide( pop(), INTERNAL_SCALE, RoundingMode.HALF_EVEN))
    }

    fun minimum() {
        if (stack.isEmpty()) return
        var min = stack[0]
        for (i in 1 until stack.size) {
            if (stack[i] < min) {
                min = stack[i]
            }
        }
        stack.clear()
        push(min)
    }

    fun maximum() {
        if (stack.isEmpty()) return
        var max = stack[0]
        for (i in 1 until stack.size) {
            if (stack[i] > max) {
                max = stack[i]
            }
        }
        stack.clear()
        push(max)
    }

    fun si() {
        if (stack.size < 3) return
        val time = pop().toDouble()
        val rate = pop().toDouble()
        val principal = pop().toDouble()
        push((principal * rate * time) / 100)
    }

    override fun toString(): String {
        var msg = ""
        for( value in stack.elements()) {
            msg += value
            msg += ";"
        }
        return msg
    }

    val df = DecimalFormat("0.#########", DecimalFormatSymbols(Locale.US))

    fun format( value: BigDecimal) : String = df.format( value.round(MathContext( PRECISION, RoundingMode.HALF_UP)).toDouble())

}