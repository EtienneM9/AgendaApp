package com.example.tp1agenda

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var calendarView: CalendarView;
    private val events: MutableMap<String, MutableList<Event>> = mutableMapOf()
    private lateinit var adapter: EventAdapter;
    private lateinit var eventclick: Button;
    private lateinit var currentdate: TextView;
    private lateinit var addEventButton: FloatingActionButton
    private lateinit var btnMonth: Button
    private lateinit var btnWeek: Button
    private lateinit var btnDay: Button

    private val calendarList: MutableList<CalendarDay> = mutableListOf() // Changed to MutableList


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Get the current date
        val currentDate = Calendar.getInstance().time

        // Format the date
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(currentDate)

        calendarView = findViewById(R.id.calendarView)
        currentdate = findViewById(R.id.selectedDateText)
        addEventButton = findViewById(R.id.btnAddEvent)
        btnMonth = findViewById(R.id.btnMonth)
        btnWeek = findViewById(R.id.btnWeek)
        btnDay = findViewById(R.id.btnDay)


        currentdate.text = formattedDate

        val calendar = Calendar.getInstance();

        calendar.set(2025, 1, 20)
        val calendarDay = CalendarDay(calendar);

        calendarDay.labelColor = R.color.clay
        calendarDay.imageResource = R.drawable.baseline_sports_tennis_24
        calendarList.add(calendarDay)

        events["20-1-2025"] = mutableListOf(
            Event("Tennis Match", "13:00", "14:00", "20-1-2025", EventCategory.SPORT),
            Event("Lunch", "14:30", "15:30", "20-1-2025", EventCategory.LUNCH)
        )
        calendarView.setCalendarDays(calendarList)

        addEventButton.setOnClickListener {
            val dialog = AddEventBottomSheet({ event ->
                // Le format attendu est "jour-mois-année" (ex: "15-3-2025")
                Log.d("DEBUG_ADDEVENT", "Nouvel événement reçu: $event")

                val dateParts = event.date.split("-")
                if (dateParts.size == 3) {
                    val day = dateParts[0].trim().toIntOrNull()
                    val month = dateParts[1].trim().toIntOrNull()
                    val year = dateParts[2].trim().toIntOrNull()

                    if (day != null && month != null && year != null) {
                        // Ajout de l'événement dans la map events
                        events.getOrPut(event.date) { mutableListOf() }.add(event)
                        Log.d("DEBUG_ADDEVENT", "Événement ajouté à la date clé ${event.date}. Liste actuelle: ${events[event.date]}")


                        // Création d'un nouvel objet CalendarDay
                        val newCalendar = Calendar.getInstance().apply {
                            set(year, month, day)
                        }
                        Log.d("DEBUG_ADDEVENT", "new calendar")

                        // Vérification si le jour est déjà présent dans calendarList
                        val existingCalendarDay = calendarList.find { cd ->
                            cd.calendar.get(Calendar.YEAR) == year &&
                                    cd.calendar.get(Calendar.MONTH) == month - 1 &&
                                    cd.calendar.get(Calendar.DAY_OF_MONTH) == day
                        }

                        Log.d("DEBUG_ADDEVENT", "existing calendar")
                        calendarView.setCalendarDays(emptyList())

                        if (existingCalendarDay != null) {
                            // Mise à jour de l'icône et de la couleur avec celles de la catégorie de l'événement
                            val iconDrawable: Drawable? = ContextCompat.getDrawable(this, event.category.iconRes)?.mutate()
                            iconDrawable?.setTint(ContextCompat.getColor(this, event.category.colorRes))
                            existingCalendarDay.imageDrawable = iconDrawable
                            existingCalendarDay.labelColor = event.category.colorRes
                            Log.d("DEBUG_ADDEVENT", "Mise à jour du CalendarDay existant pour le $day-$month-$year")
                        } else {
                            val newCalendarDay = CalendarDay(newCalendar).apply {
                                imageResource = event.category.iconRes
                                val iconDrawable: Drawable? = ContextCompat.getDrawable(this@MainActivity, event.category.iconRes)?.mutate()
                                imageDrawable = iconDrawable
                                labelColor = event.category.colorRes
                            }

                            calendarList.add(newCalendarDay)

                            Log.d("DEBUG_ADDEVENT", "Nouv" +
                                    "eau CalendarDay ajouté pour le $day-$month-$year" )
                        }

                        Log.d("DEBUG_ADDEVENT", "maj calendarview")

                        calendarView.setCalendarDays(calendarList)  // Recharge complète

                    } else {
                        Log.d("MainActivity", "Mise à jour du CalendarDay existant pour le $day-$month-$year")
                        Toast.makeText(this, "Date invalide", Toast.LENGTH_SHORT).show()
                        Log.d("DEBUG_ADDEVENT", "Le CalendarDay pour le $day-$month-$year existe déjà")

                    }
                } else {
                    Toast.makeText(this, "Format de date incorrect", Toast.LENGTH_SHORT).show()
                    Log.d("MainActivity", "Format de date incorrect pour: ${event.date}")

                }
            }, findViewById(android.R.id.content))
            dialog.show(supportFragmentManager, "AddEventBottomSheet")
        }


        calendarView.setOnCalendarDayClickListener(object: OnCalendarDayClickListener{
            override fun onClick(calendarDay: CalendarDay) {
                val day = String.format("%2d", calendarDay.calendar.get(Calendar.DAY_OF_MONTH))
                val month = String.format("%2d", calendarDay.calendar.get(Calendar.MONTH)).trim().toIntOrNull()
                val year = calendarDay.calendar.get(Calendar.YEAR)
                val dateKey = "$day-$month-$year"
                Log.d("DEBUG_DATE", dateKey);

                val dayEvents = events[dateKey] ?: mutableListOf()
                val dialog = TrajetsDialogFragment(dayEvents, findViewById(android.R.id.content))
                dialog.show(supportFragmentManager, "TrajetsDialog")

                if (dayEvents.isNotEmpty()) {
                    Toast.makeText(baseContext, "${dayEvents.size} événements aujourd'hui", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Rien aujourd'hui", Toast.LENGTH_SHORT).show()
                }


                calendarView.setOnPreviousPageChangeListener(object: OnCalendarPageChangeListener{
                    override fun onChange() {
                        val month = String.format("%02d", calendarView.currentPageDate.get(Calendar.MONTH))
                        val year = String.format("%02d", calendarView.currentPageDate.get(Calendar.YEAR))
                        Toast.makeText(baseContext, "$month/$year", Toast.LENGTH_SHORT).show()
                    }
                })

                calendarView.setOnForwardPageChangeListener(object: OnCalendarPageChangeListener{
                    override fun onChange() {
                        val month = String.format("%02d", calendarView.currentPageDate.get(Calendar.MONTH))
                        val year = String.format("%02d", calendarView.currentPageDate.get(Calendar.YEAR))
                        Toast.makeText(baseContext, "$month/$year", Toast.LENGTH_SHORT).show()
                    }
                })

            }
        })
    }

    fun removeEvent(event: Event, date: String) {
        // Remove the event from the map
        events[date]?.remove(event)

        // If the list is empty, remove the date from the map
        if (events[date]?.isEmpty() == true) {
            events.remove(date)
        }

        // Update the calendar
        updateCalendar()
    }

    private fun updateCalendar() {
        calendarList.clear()
        events.keys.forEach { dateKey ->
            val dateParts = dateKey.split("-")
            if (dateParts.size == 3) {
                val day = dateParts[0].trim().toIntOrNull()
                val month = dateParts[1].trim().toIntOrNull()
                val year = dateParts[2].trim().toIntOrNull()

                if (day != null && month != null && year != null) {
                    val newCalendar = Calendar.getInstance().apply {
                        set(year, month, day)
                    }

                    val newCalendarDay = CalendarDay(newCalendar).apply {
                        val firstEvent = events[dateKey]?.firstOrNull()
                        if (firstEvent != null) {
                            imageResource = firstEvent.category.iconRes
                            val iconDrawable: Drawable? = ContextCompat.getDrawable(
                                this@MainActivity,
                                firstEvent.category.iconRes
                            )?.mutate()
                            iconDrawable?.setTint(
                                ContextCompat.getColor(
                                    this@MainActivity,
                                    firstEvent.category.colorRes
                                )
                            )
                            imageDrawable = iconDrawable
                            labelColor = firstEvent.category.colorRes
                        }
                    }
                    calendarList.add(newCalendarDay)
                }
            }
        }
        calendarView.setCalendarDays(calendarList)
    }

}