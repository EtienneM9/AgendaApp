package com.example.tp1agenda

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class AddEventBottomSheet(private val onValidate: (Event) -> Unit, private val mainView: View) :
    BottomSheetDialogFragment() {

    private lateinit var titleInput: EditText
    private lateinit var datePickerInput: TextInputEditText
    private lateinit var startTimePickerInput: TextInputEditText
    private lateinit var endTimePickerInput: TextInputEditText
    private lateinit var categoryChipGroup: ChipGroup
    private lateinit var validateButton: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_add_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements
        titleInput = view.findViewById(R.id.eventTitleInput)
        datePickerInput = view.findViewById(R.id.eventDatePicker)
        startTimePickerInput = view.findViewById(R.id.eventStartTimePicker)
        endTimePickerInput = view.findViewById(R.id.eventEndTimePicker)
        categoryChipGroup = view.findViewById(R.id.eventCategoryChipGroup)
        validateButton = view.findViewById(R.id.validateEventButton)

        // Set up category chips
        setupCategoryChips()

        // Set up date picker
        setupDatePicker()

        // Set up time pickers
        setupTimePickers()

        // Set up validate button
        validateButton.setOnClickListener {
            validateAndCreateEvent()
        }
    }

    private fun setupCategoryChips() {
        EventCategory.values().forEach { category ->
            val chip = Chip(requireContext()).apply {
                text = category.name
                isCheckable = true
                isClickable = true
                chipBackgroundColor = ContextCompat.getColorStateList(context, R.color.light_blue)
                setTextColor(ContextCompat.getColor(context, R.color.white))
            }
            categoryChipGroup.addView(chip)
        }
    }

    private fun setupDatePicker() {
        datePickerInput.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Sélectionner une date")
                .build()

            datePicker.addOnPositiveButtonClickListener { selectedDate ->
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = selectedDate
                val dateFormat = SimpleDateFormat("dd-M-yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(calendar.time)
                datePickerInput.setText(formattedDate)
            }

            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }
    }

    private fun setupTimePickers() {
        startTimePickerInput.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Sélectionner l'heure de début")
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                val formattedTime = String.format("%02d:%02d", hour, minute)
                startTimePickerInput.setText(formattedTime)
            }

            timePicker.show(parentFragmentManager, "START_TIME_PICKER")
        }

        endTimePickerInput.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Sélectionner l'heure de fin")
                .build()

            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                val formattedTime = String.format("%02d:%02d", hour, minute)
                endTimePickerInput.setText(formattedTime)
            }

            timePicker.show(parentFragmentManager, "END_TIME_PICKER")
        }
    }

    override fun onStart() {
        super.onStart()
        applyBlurEffect(mainView, 20f)
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        // Retirer le flou quand le dialog est fermé
        removeBlurEffect(mainView)
    }

    private fun validateAndCreateEvent() {
        val title = titleInput.text.toString()
        val date = datePickerInput.text.toString()
        val startTime = startTimePickerInput.text.toString()
        val endTime = endTimePickerInput.text.toString()

        val selectedChipId = categoryChipGroup.checkedChipId
        val selectedChip = categoryChipGroup.findViewById<Chip>(selectedChipId)
        val category = if (selectedChip != null) {
            EventCategory.valueOf(selectedChip.text.toString())
        } else {
            null
        }

        if (title.isNotEmpty() && date.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty() && category != null) {

            val dateParts = date.split("-")
            val day = dateParts[0].trim().toIntOrNull()
            val month = dateParts[1].trim().toIntOrNull()
            val year = dateParts[2].trim().toIntOrNull()

            if (day != null && month != null && year != null) {
                // Subtract 1 from the month here
                val eventDate = "$day-${month - 1}-$year"


                val event = Event(title, startTime, endTime, eventDate, category)
                Log.d(
                    "DEBUG_ADDEVENT",
                    title + " " + startTime + " " + date + " " + category + " " + endTime
                )
                onValidate(event)
                dismiss()
            } else {
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun applyBlurEffect(view: View, blurRadius: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(
                RenderEffect.createBlurEffect(
                    blurRadius,
                    blurRadius,
                    Shader.TileMode.CLAMP
                )
            )
        }
    }

    fun removeBlurEffect(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            view.setRenderEffect(null)
        }
    }

}