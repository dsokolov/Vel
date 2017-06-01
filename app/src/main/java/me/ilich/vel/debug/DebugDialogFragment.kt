package me.ilich.vel.debug

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.widget.ArrayAdapter

class DebugDialogFragment : DialogFragment() {

    companion object {
        fun show(fm: FragmentManager) {
            val d = DebugDialogFragment()
            d.show(fm, "debug dialog")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val d = AlertDialog.Builder(context)
                .setTitle("Title")
                .setMessage("Message")
                .setPositiveButton("Positive") { dialog, which -> }
                .setNegativeButton("Negative") { dialog, which -> }
                .setNeutralButton("Neutral") { dialog, which -> }
                .setAdapter(ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, arrayOf("Item1", "Item2", "Item3"))) { dialog, which ->

                }
                .create()
        return d
    }
}