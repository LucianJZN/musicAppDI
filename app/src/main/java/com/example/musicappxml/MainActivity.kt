package com.example.musicappxml

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var reproduciendo = false  // Para saber si se está reproduciendo
    private lateinit var manejador: Handler
    private lateinit var tarea: Runnable
    private var progreso = 25  // El progreso inicial
    private val duracionTotal = 127  // Duración total de la canción en segundos

    override fun onCreate(guardadoEstado: Bundle?) {
        super.onCreate(guardadoEstado)
        setContentView(R.layout.activity_main)

        // Configuración de los ajustes de la ventana
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Establecer nombre del disco y del grupo
        val nombreGrupo = findViewById<TextView>(R.id.nombreGrupo)
        val tituloCancion = findViewById<TextView>(R.id.tituloCancion)
        nombreGrupo.text = "Louis Armstrong"
        tituloCancion.text = "What a wonderful world"

        // Establecer imagen del disco
        val imgMusica = findViewById<ImageView>(R.id.imgMusica)
        imgMusica.setImageResource(R.drawable.louisarmstrong)

        // Configurar la barra de progreso de la música
        val barraProgreso = findViewById<SeekBar>(R.id.barraProgresion)
        barraProgreso.max = duracionTotal
        barraProgreso.progress = progreso  // Progreso inicial

        // Configurar la barra de volumen
        val barraVolumen = findViewById<SeekBar>(R.id.barraMusica)
        barraVolumen.max = 100
        barraVolumen.progress = 75  // Volumen inicial

        // Configurar el manejador
        manejador = Handler(Looper.getMainLooper())

        // Configurar la imagen de reproducir/pausar
        val imagenReproducir = findViewById<ImageView>(R.id.imgPlay)

        // Listener para el botón de Reproducir/Detener
        imagenReproducir.setOnClickListener {
            if (reproduciendo) {
                // Si está en reproducción, cambia la imagen a "Reproducir" y detiene el progreso
                imagenReproducir.setImageResource(R.drawable.play_button)
                detenerProgreso()
            } else {
                // Si no está en reproducción, cambia la imagen a "Detener" y empieza a sumar al progreso
                imagenReproducir.setImageResource(R.drawable.pause_button)
                iniciarProgreso(barraProgreso)
            }
            reproduciendo = !reproduciendo  // Cambia el estado
        }

        // Restaurar el estado guardado
        if (guardadoEstado != null) {
            progreso = guardadoEstado.getInt("progreso", 25)
            barraProgreso.progress = progreso  // Restablecer el progreso
            reproduciendo = guardadoEstado.getBoolean("reproduciendo", false)
            if (reproduciendo) {
                imagenReproducir.setImageResource(R.drawable.pause_button)
                iniciarProgreso(barraProgreso)  // Reiniciar el progreso si estaba reproduciendo
            } else {
                imagenReproducir.setImageResource(R.drawable.play_button)
            }
        }
    }

    // Guardar el estado en caso de que la actividad se destruya
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("progreso", progreso)
        outState.putBoolean("reproduciendo", reproduciendo)
    }

    // Función para empezar a sumar progreso
    private fun iniciarProgreso(barraProgreso: SeekBar) {
        // TextViews para el tiempo avanzado y tiempo restante
        val tiempoAvanzado = findViewById<TextView>(R.id.tiempoAvanzado)
        val tiempoRestante = findViewById<TextView>(R.id.tiempoRestante)

        tarea = object : Runnable {
            override fun run() {
                if (progreso < barraProgreso.max) {
                    progreso++
                    barraProgreso.progress = progreso
                    actualizarTiempo(tiempoAvanzado, tiempoRestante)  // Actualiza los TextViews
                    manejador.postDelayed(this, 1000)  // Actualiza cada 1 segundo
                } else {
                    detenerProgreso()  // Detén cuando llegue al máximo
                }
            }
        }
        manejador.post(tarea)  // Inicia el proceso de actualización
    }

    // Función para detener el progreso
    private fun detenerProgreso() {
        manejador.removeCallbacks(tarea)  // Detén la actualización del progreso
    }

    // Función para actualizar los TextViews del tiempo avanzado y tiempo restante
    private fun actualizarTiempo(tiempoAvanzado: TextView, tiempoRestante: TextView) {
        val minutosAvanzados = progreso / 60
        val segundosAvanzados = progreso % 60
        tiempoAvanzado.text = String.format("%02d:%02d", minutosAvanzados, segundosAvanzados)

        val tiempoRestanteTotal = duracionTotal - progreso
        val minutosRestantes = tiempoRestanteTotal / 60
        val segundosRestantes = tiempoRestanteTotal % 60
        tiempoRestante.text = String.format("%02d:%02d", minutosRestantes, segundosRestantes)
    }
}
