package mx.edu.ittepic.ladm_u4_tarea1

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CallLog
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    val permisoCalendario = 1
    val permisoLlamada = 2
    var evento = listOf(CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART).toTypedArray()
    var listaEventos = ArrayList<String>()
    var llamada = listOf(CallLog.Calls._ID, CallLog.Calls.TYPE, CallLog.Calls.DATE, CallLog.Calls.NUMBER).toTypedArray()
    var listaLlamadas = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            //  verificar si se tiene el permiso LECTURA EVENTOS CALENDARIO
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_DENIED){
                // solicitar el permiso LECTURA EVENTOS CALENDARIO
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR), permisoCalendario)
            } else {
                eventos()
            }
        }

        button2.setOnClickListener {
            //  verificar si se tiene el permiso LECTURA REGISTRO DE LLAMADAS
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED){
                // solicitar el permiso LECTURA REGISTRO DE LLAMADAS
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), permisoLlamada)
            } else {
                llamadas()
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==permisoCalendario)
            eventos()
        if (requestCode==permisoLlamada)
            llamadas()
    }

    private fun eventos() {
        val seleccion = "((${CalendarContract.Calendars.ACCOUNT_NAME} = ?) AND" +
                "(${CalendarContract.Calendars.ACCOUNT_TYPE} = ?) AND" +
                "(${CalendarContract.Calendars.OWNER_ACCOUNT} = ?))"
        val argumentosSeleccion = arrayOf("ismael97lopez@gmail.com", "com.google", "ismael97lopez@gmail.com")
        val cursor = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            evento, seleccion, argumentosSeleccion, null
        )!!
        listaEventos.clear()
        if (cursor.moveToFirst()){
            // Obtener el indice de los elementos a recuperar
            val posicionTitulo = cursor.getColumnIndex(CalendarContract.Events.TITLE)
            val posicionFecha = cursor.getColumnIndex(CalendarContract.Events.DTSTART)

            // Recuperación de los datos
            do {
                val fecha = convertMStoDate(cursor.getLong(posicionFecha))
                listaEventos.add("Titulo: ${cursor.getString(posicionTitulo)}\n" +
                        "Fecha: ${fecha}")
            } while (cursor.moveToNext())
        }
        lista.adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, listaEventos)
        textView.setText("Se esta mostrando: Eventos")
    }

    private fun convertMStoDate(fechaMS: Long): String {
        if (fechaMS == null) return ""
        val calendario = Calendar.getInstance(Locale.getDefault())
        calendario.timeInMillis = fechaMS
        return android.text.format.DateFormat.format("dd MM yyyy", calendario).toString()
    }

    private fun llamadas() {
        /* tipos de llamada:
        1 -> Llamada entrante
        2 -> Saliente
        3 -> Perdida
        4 -> Correo de voz
        5 -> Rachazadas
        6 -> Números bloqueados
         */
        val cursor = contentResolver.query(
            CallLog.Calls.CONTENT_URI, llamada, null, null, null
            //CallLog.Calls.TYPE + " = ?", arrayOf("1"), "${CallLog.Calls.LAST_MODIFIED}"
        )!!
        listaLlamadas.clear()
        if (cursor.moveToFirst()){
            var posicionNumero = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            var posicionTipo = cursor.getColumnIndex(CallLog.Calls.TYPE)

            do{
                listaLlamadas.add("Número: ${cursor.getString(posicionNumero)}\n" +
                        "Tipo: ${cursor.getString(posicionTipo)}")
            } while (cursor.moveToNext())
        }
        lista.adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, listaLlamadas)
        textView.setText("Se esta mostrando: Llamadas")
    }
}