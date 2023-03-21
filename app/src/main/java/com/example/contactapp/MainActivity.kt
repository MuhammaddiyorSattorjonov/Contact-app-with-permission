package com.example.contactapp


import android.Manifest
import android.Manifest.permission.READ_CONTACTS
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.contactapp.adapter.RvAdapter
import com.example.contactapp.databinding.ActivityMainBinding
import com.example.contactapp.models.Contact
import com.example.contactapp.swipe.MyButton
import com.example.contactapp.swipe.MyButtonClickListener
import com.example.contactapp.swipe.MySwipeHelper
import com.github.florent37.runtimepermission.kotlin.askPermission

class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    lateinit var adapter: RvAdapter
    lateinit var contactList: ArrayList<Contact>
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.rv.setHasFixedSize(true)

        val swipe = object : MySwipeHelper(this, binding.rv, 120){

            override fun instantiaveMyButton(
                viewHolder: RecyclerView.ViewHolder,
                buffer: MutableList<MyButton>,
            )  {
                //add button
                buffer.add(
                    MyButton(this@MainActivity,
                        "Sms",
                        30,
                        R.drawable.ic_baseline_sms_24,
                        Color.parseColor("#FFDD2371"),
                        object : MyButtonClickListener {
                            override fun onclick(position: Int) {
                                val intent = Intent(this@MainActivity, MainActivity2::class.java)
                                intent.putExtra("key", contactList[position])
                                startActivity(intent)
                            }
                        })
                )
                buffer.add(
                    MyButton(this@MainActivity,
                        "Call",
                        30,
                        R.drawable.ic_baseline_local_phone_24,
                        Color.parseColor("#FFF8CA2A"),
                        object : MyButtonClickListener {
                            override fun onclick(position: Int) {
                                Toast.makeText(this@MainActivity, "Update id $position", Toast.LENGTH_SHORT).show()
                                telefonQilish(position)
                            }
                        })
                )
            }


        }
        readContact()
    }

    @SuppressLint("Range")
    @RequiresApi(Build.VERSION_CODES.O)
    fun readContact(){
        contactList = ArrayList()
        askPermission(READ_CONTACTS){
            //all permissions already granted or just granted
            val contacts = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null)
            while (contacts!!.moveToNext()){
                val contact = Contact(
                    contacts!!.getString(contacts!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                    contacts!!.getString(contacts!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                )

                contactList.add(contact)
            }
            contacts!!.close()

            adapter = RvAdapter(contactList)
            binding.rv.adapter = adapter
        }.onDeclined { e ->
            if (e.hasDenied()) {

                AlertDialog.Builder(this)
                    .setMessage("Ruxsat bermasangiz ilova ishlay olmaydi ruxsat bering...")
                    .setPositiveButton("yes") { dialog, which ->
                        e.askAgain();
                    }
                    .setNegativeButton("no") { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }

            if(e.hasForeverDenied()) {
                e.goToSettings();
            }
        }
    }
    private fun telefonQilish(position:Int) {

        askPermission(Manifest.permission.CALL_PHONE){
            //all permissions already granted or just granted
            val phonNumber = contactList[position].number
            val intent = Intent(Intent(Intent.ACTION_CALL))
            intent.data = Uri.parse("tel:$phonNumber")
            startActivity(intent)
        }.onDeclined { e ->
            if (e.hasDenied()) {

                AlertDialog.Builder(this)
                    .setMessage("Ruxsat bermasangiz ilova ishlay olmaydi ruxsat bering...")
                    .setPositiveButton("yes") { dialog, which ->
                        e.askAgain();
                    } //ask again
                    .setNegativeButton("no") { dialog, which ->
                        dialog.dismiss();
                    }
                    .show();
            }

            if(e.hasForeverDenied()) {
                //the list of forever denied permissions, user has check 'never ask again'

                // you need to open setting manually if you really need it
                e.goToSettings();
            }
        }
    }

}