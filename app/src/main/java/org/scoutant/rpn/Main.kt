package org.scoutant.rpn

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

class Main : Activity(), Update {

    private val buffer: Buffer = Buffer()
    private var calculator: Calculator = Calculator()
    private val state : State by lazy { State( this) }

    private var bv: TextView? = null
    private val ids = listOf( R.id.stack0, R.id.stack1, R.id.stack2, R.id.stack3, R.id.stack4, R.id.stack5, R.id.stack6, R.id.stack7 )
    private var svs : Array<TextView?> = arrayOfNulls(ids.size)

    lateinit var vibrator:Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        bv = findViewById<TextView> (R.id.buffer)
       for (i in  0..ids.size-1) svs[i] = findViewById<TextView>( ids[i])
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    override fun onResume() {
        super.onResume()
        calculator = Calculator( state.cache())
        update()
    }

    private var previous: String = ""

    override fun update() {

        if (buffer.isEmpty()) bv!!.visibility = View.GONE
        else {
            bv!!.visibility = View.VISIBLE
            bv!!.text = buffer.get()
        }

        val nb = Math.min( ids.size-1, calculator.size()-1)
        for (i in 0..nb) {
            svs[i]?.text = ""+ calculator.format( calculator.stack[calculator.size()-1-i])
        }
        for (i in nb+1..ids.size-1) svs[i]?.text = ""

        previous = state.cache();
        state.cache( calculator.toString())

    }


    fun push() {
        vibrate()
        if (buffer.isEmpty()) return
        calculator.push( buffer.get())
        buffer.reset()
    }

    fun digit(@Suppress("UNUSED_PARAMETER") v: View) {
        val digit:String = v.tag as String
        Log.d("keyboard", "digit : $digit")
        buffer.append( digit)
        vibrate_short()
        update()
    }

    fun enter(@Suppress("UNUSED_PARAMETER") v: View) {
        if (buffer.isEmpty()) {
            calculator.dup()
            vibrate()
        }
        else push()
        update()
    }

    fun drop(@Suppress("UNUSED_PARAMETER") v: View)   { push(); calculator.drop(); update(); }
    fun sqrt(@Suppress("UNUSED_PARAMETER") v:View) {
        push()
        try {
            calculator.sqrt()
            update()
        } catch (e: ArithmeticException) {
            toast( "Only for positive numbers.")
        }
    }
    fun minimum(@Suppress("UNUSED_PARAMETER") v: View) {
        push()
        calculator.minimum()
        update()
    }

    fun maximum(@Suppress("UNUSED_PARAMETER") v: View) {
        push()
        calculator.maximum()
        update()
    }

    fun si(@Suppress("UNUSED_PARAMETER") v: View) {
        push()
        calculator.si()
        update()
    }

    fun reciprocal(@Suppress("UNUSED_PARAMETER") v:View) {
        push()
        try {
            calculator.reciprocal()
            update()
        } catch (e: ArithmeticException) {
            toast( "UNDEFINED.")
        }
    }

    fun swap(@Suppress("UNUSED_PARAMETER") v: View)   { push(); calculator.swap(); update(); }
    fun add(@Suppress("UNUSED_PARAMETER") v: View)    { push(); calculator.add(); update(); }
    fun subtract(@Suppress("UNUSED_PARAMETER") v: View) { push(); calculator.subtract(); update(); }
    fun multiply(@Suppress("UNUSED_PARAMETER") v: View) { push(); calculator.multiply(); update(); }
    fun divide(@Suppress("UNUSED_PARAMETER") v: View) {
        push()
        try {
            calculator.divide()
            update()
        } catch (e: ArithmeticException) {
            toast( "UNDEFINED.")
        }
    }

    fun power(@Suppress("UNUSED_PARAMETER") v: View) {
        push()
        calculator.power()
        update()
    }

    fun delete(@Suppress("UNUSED_PARAMETER") v: View) {
        if (buffer.isNotEmpty()) vibrate_short()
        buffer.delete()
        update()
    }
    fun dot(@Suppress("UNUSED_PARAMETER") v: View)    { vibrate_short(); buffer.dot(); update(); }


    fun toast(msg:String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG)
            .show()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        this.finish()
        startActivity( Intent( this, this.javaClass))
    }

    fun vibrate_short() {
        vibrator.vibrate( VibrationEffect.createWaveform(longArrayOf(20,20 ), intArrayOf(120, 80), -1))
         }

    fun vibrate() {
        vibrator.vibrate( VibrationEffect.createWaveform(longArrayOf(60,60 ), intArrayOf(180, 100), -1))
         }
}