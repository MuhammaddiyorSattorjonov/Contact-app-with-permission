package com.example.contactapp

import android.Manifest.permission.SEND_SMS
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.contactapp.databinding.ActivityMain2Binding
import com.example.contactapp.models.Contact
import com.github.florent37.runtimepermission.kotlin.askPermission

class MainActivity2 : AppCompatActivity() {
    private val binding by lazy { ActivityMain2Binding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val contact = intent.getSerializableExtra("key") as Contact
        binding.ismi.text = contact.name
        binding.raqami.text = contact.number

        binding.btnSend.setOnClickListener {
            askPermission(android.Manifest.permission.SEND_SMS) {

                var sms = SmsManager.getDefault()
                sms.sendTextMessage(contact.number,
                    null, binding.about.text.toString(),
                    null, null)
                Toast.makeText(this, "Xabar jo'natildi", Toast.LENGTH_SHORT).show()

            }.onDeclined { e ->
                if (e.hasDenied()) {

                    AlertDialog.Builder(this)
                        .setMessage("Ruxsat bermasangiz ilova ishlay olmaydi ruxsat bering...")
                        .setPositiveButton("Xa") { dialog, which ->
                            e.askAgain();
                        } //ask again
                        .setNegativeButton("Yo'q") { dialog, which ->
                            dialog.dismiss();
                        }
                        .show();
                }

                binding.back.setOnClickListener {
                    finish()
                }
            }
        }
    }
}
