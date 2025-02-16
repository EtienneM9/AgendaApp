package com.example.tp1agenda

import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment



class TrajetsDialogFragment(private val events: MutableList<Event>,private val mainView: View) : BottomSheetDialogFragment(), EventDeleteListener {


    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        setStyle(STYLE_NORMAL, R.style.DialogTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_trajets, container, false)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog?.window?.setDimAmount(0f)
        view.setBackgroundColor(Color.TRANSPARENT)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_trajets)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = EventAdapter(events, this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recycler_trajets)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = EventAdapter(events, this)
        recyclerView.adapter = adapter
    }

    override fun onDeleteEvent(event: Event, date: String) {
        // Remove the event from the list
        events.remove(event)
        // Notify the adapter
        adapter.notifyDataSetChanged()

        // Remove the event from the map in MainActivity
        if (activity is MainActivity) {
            (activity as MainActivity).removeEvent(event, date)
        }
    }

    override fun onStart() {
        super.onStart()
        // Ajouter le flou quand le dialog s'affiche
        applyBlurEffect(mainView, 20f)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setDimAmount(0f)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        dialog?.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDismiss(dialog: android.content.DialogInterface) {
        super.onDismiss(dialog)
        // Retirer le flou quand le dialog est fermé
        removeBlurEffect(mainView)
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

    override fun getTheme(): Int{
        return R.style.DialogTheme
    }
}
