package sovonthy.com.kotlinmessenger

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import java.util.*


class RegisterActivity : AppCompatActivity() {

    companion object {
        val TAG = "RegisterActivity"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        register.setOnClickListener {
            performRegister()

        }

       already_have_account.setOnClickListener {
           val intent = Intent(this, LoginActivity::class.java)
           startActivity(intent)
       }

        selectphoto_button_register.setOnClickListener {
            Log.d("RegisterActivity", "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }

    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceed and check what the selected image was.....
            Log.d("RegisterActivity","Photo was selected")

            selectedPhotoUri = data.data

           val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            selectphoto_imageview_register.setImageBitmap(bitmap)

            selectphoto_button_register.alpha = 0f

        }
    }

    private fun performRegister(){
        val email = email.text.toString()
        val password = password.text.toString()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter text in email/pw", Toast.LENGTH_LONG).show()
            return
        }

        Log.d("RegisterActivity", "Email is: " + email)
        Log.d("RegisterActivity","Password is: "+ password)

        //Firebase authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener

                uploadImageToFirebaseStrorage()

                //else if successful
                Log.d("RegisterActivity", "Successfully created user with uid: ${it.result.user.uid}")
            }.addOnFailureListener{
                Log.d("RegisterActivity", "Failed created user: ${it.message}")
                Toast.makeText(this, "Failed to created user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadImageToFirebaseStrorage(){
      if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("RegisterActivity", "File Location: $it")
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
            .addOnFailureListener {
               Log.d(TAG, "Failed to upload image to storage: ${it.message}")
            }
    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
       val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid")
        val user = User(uid, username.text.toString(), profileImageUrl)
        ref.setValue(user)
            .addOnSuccessListener {
                val intent = Intent(this, LatestMessagesActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener{
                Toast.makeText(this, "Failed to set value to database", Toast.LENGTH_LONG).show()
            }

    }
}

class User(val uid: String, val username: String, val profileImageUrl: String)