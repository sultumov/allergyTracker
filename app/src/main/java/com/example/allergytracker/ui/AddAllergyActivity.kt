import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.allergytracker.R
import com.example.allergytracker.data.AllergyRecord
import java.text.SimpleDateFormat
import java.util.*

class AddAllergyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_allergy)

        val viewModel = AllergyViewModel(application)

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val symptoms = findViewById<EditText>(R.id.editSymptoms).text.toString()
            val triggers = findViewById<EditText>(R.id.editTriggers).text.toString()
            val medication = findViewById<EditText>(R.id.editMedication).text.toString()

            val record = AllergyRecord(
                date = SimpleDateFormat("yyyy-MM-dd HH:mm").format(Date()),
                symptoms = symptoms,
                triggers = triggers,
                medication = medication
            )

            viewModel.addRecord(record)
            finish()
        }
    }
}