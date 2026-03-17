package com.audiomaster.analyzer

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.topjohnwu.superuser.Shell
import android.net.Uri
import android.content.Intent
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var khzText: TextView
    private lateinit var bufferText: TextView
    private lateinit var dacText: TextView
    private lateinit var hifiText: TextView
    private lateinit var deviceText: TextView
    private lateinit var sysInfoText: TextView
    private lateinit var modeText: TextView
    private lateinit var statusDot: View
    private lateinit var moduleMissingLayout: View
    private lateinit var mainLayout: View

    private val modulePath = "/data/adb/modules/kyy_audio_selene"
    private val actionScript = "$modulePath/webroot/action.sh"
    
    private val handler = Handler(Looper.getMainLooper())
    private var isBusy = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        khzText = findViewById(R.id.khz_value)
        bufferText = findViewById(R.id.buffer_value)
        dacText = findViewById(R.id.resampler_value)
        hifiText = findViewById(R.id.hifi_value)
        deviceText = findViewById(R.id.device_name)
        sysInfoText = findViewById(R.id.sys_info)
        modeText = findViewById(R.id.active_mode_name)
        statusDot = findViewById(R.id.status_dot)
        moduleMissingLayout = findViewById(R.id.module_missing_container)
        mainLayout = findViewById(R.id.main_content)

        setupButtons()
        
        // Root and Module Check
        checkEnvironment()
    }

    private fun checkEnvironment() {
        val isRooted = Shell.getShell().isRoot
        if (!isRooted) {
            mainLayout.visibility = View.GONE
            moduleMissingLayout.visibility = View.VISIBLE
            val msg = findViewById<TextView>(R.id.module_missing_text)
            msg.text = "Root access denied. APK ini butuh akses root untuk berfungsi."
            return
        }

        val check = Shell.cmd("test -f $actionScript && echo 'exists'").exec().out
        if (check.isEmpty() || check[0] != "exists") {
            mainLayout.visibility = View.GONE
            moduleMissingLayout.visibility = View.VISIBLE
            
            // Add download button logic
            val downloadBtn = findViewById<Button>(R.id.btn_download_module)
            downloadBtn?.visibility = View.VISIBLE
            downloadBtn?.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Luckyfr1945/module-audio"))
                startActivity(intent)
            }
        } else {
            mainLayout.visibility = View.VISIBLE
            moduleMissingLayout.visibility = View.GONE
            startDataUpdates()
        }
    }

    private fun setupButtons() {
        findViewById<CardView>(R.id.mode_normal).setOnClickListener { setMode("normal") }
        findViewById<CardView>(R.id.mode_bass).setOnClickListener { setMode("bass") }
        findViewById<CardView>(R.id.mode_gaming).setOnClickListener { setMode("gaming") }
        findViewById<CardView>(R.id.mode_hifi).setOnClickListener { setMode("hifi") }
        findViewById<CardView>(R.id.mode_cinema).setOnClickListener { setMode("cinema") }
    }

    private fun setMode(mode: String) {
        if (isBusy) return
        isBusy = true
        
        Thread {
            val result = Shell.cmd("sh $actionScript set_mode $mode").exec().out.joinToString("\n")
            runOnUiThread {
                try {
                    val json = extractJson(result)
                    if (json != null && json.optBoolean("success", false)) {
                        updateUI(json)
                        Toast.makeText(this, "Profile ${mode.capitalize()} applied!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to apply profile", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                isBusy = false
            }
        }.start()
    }

    private fun startDataUpdates() {
        handler.post(object : Runnable {
            override fun run() {
                updateAudioData()
                handler.postDelayed(this, 1500) // Much snappier
            }
        })
        
        // Start pulsing animation for LIVE indicators
        startPulsingAnimations()
    }

    private fun startPulsingAnimations() {
        val liveKhz = findViewById<View>(R.id.live_indicator_khz)
        val liveBuffer = findViewById<View>(R.id.live_indicator_buffer)
        
        val pulse = object : Runnable {
            var growing = true
            override fun run() {
                val alpha = if (growing) 1.0f else 0.4f
                liveKhz.animate().alpha(alpha).setDuration(800).start()
                liveBuffer.animate().alpha(alpha).setDuration(800).start()
                growing = !growing
                handler.postDelayed(this, 800)
            }
        }
        handler.post(pulse)
    }

    private fun updateAudioData() {
        if (isBusy) return
        
        Thread {
            val result = Shell.cmd("sh $actionScript get_mode").exec().out.joinToString("\n")
            runOnUiThread {
                try {
                    val json = extractJson(result)
                    if (json != null) {
                        updateUI(json)
                    }
                } catch (e: Exception) {
                    // Ignore errors during background update
                }
            }
        }.start()
    }

    private fun updateUI(json: JSONObject) {
        val srate = json.optString("srate", "48")
        val buffer = json.optString("buffer", "-")
        val isFrames = json.optBoolean("buffer_is_frames", false)
        val gain = json.optString("gain", "4")
        val hifi = json.optString("hifi", "OFF")
        val device = json.optString("device", "Unknown")
        val sysinfo = json.optString("sysinfo", "")
        val mode = json.optString("mode", "normal")

        khzText.text = srate
        bufferText.text = if (isFrames) "$buffer ms" else "$buffer" // Script currently returns frames but we label it ms or frames
        if (isFrames) {
            // If it's real frames, maybe suffix with f or just frames
            bufferText.text = "$buffer frames"
        } else {
            bufferText.text = "$buffer ms"
        }
        
        dacText.text = gain
        hifiText.text = hifi
        deviceText.text = device
        sysInfoText.text = sysinfo
        modeText.text = mode.uppercase()
        
        // Mode Cards Highlighting
        val modeCards = mapOf(
            "normal" to Triple(R.id.mode_normal, R.id.icon_bg_normal, "#3B82F6"),
            "bass" to Triple(R.id.mode_bass, R.id.icon_bg_bass, "#F97316"),
            "gaming" to Triple(R.id.mode_gaming, R.id.icon_bg_gaming, "#10B981"),
            "hifi" to Triple(R.id.mode_hifi, R.id.icon_bg_hifi, "#8B5CF6"),
            "cinema" to Triple(R.id.mode_cinema, R.id.icon_bg_cinema, "#F43F5E")
        )

        val activeColorHex = modeCards[mode]?.third ?: "#3B82F6"
        val activeColor = Color.parseColor(activeColorHex)
        val activeColorDim = Color.parseColor(activeColorHex.replace("#", "#22"))
        val defaultCardColor = Color.parseColor("#18181B")
        val defaultIconColor = Color.parseColor("#27272A")
        
        modeCards.forEach { (m, triple) ->
            val card = findViewById<CardView>(triple.first)
            val iconBg = findViewById<CardView>(triple.second)
            if (m == mode) {
                card.setCardBackgroundColor(activeColorDim)
                iconBg.setCardBackgroundColor(activeColor)
            } else {
                card.setCardBackgroundColor(defaultCardColor)
                iconBg.setCardBackgroundColor(defaultIconColor)
            }
        }

        modeText.setTextColor(activeColor)
        statusDot.setBackgroundColor(activeColor)
    }

    private fun extractJson(output: String): JSONObject? {
        val firstBrace = output.indexOf('{')
        val lastBrace = output.lastIndexOf('}')
        if (firstBrace != -1 && lastBrace != -1 && lastBrace >= firstBrace) {
            val jsonStr = output.substring(firstBrace, lastBrace + 1)
            return JSONObject(jsonStr)
        }
        return null
    }
}
