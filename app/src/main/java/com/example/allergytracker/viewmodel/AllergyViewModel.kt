import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.allergytracker.data.AllergyRecord
import com.example.allergytracker.data.AppDatabase
import kotlinx.coroutines.launch

class AllergyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val _records = MutableLiveData<List<AllergyRecord>>()
    val records: LiveData<List<AllergyRecord>> get() = _records

    init {
        loadRecords()
    }

    private fun loadRecords() {
        viewModelScope.launch {
            _records.value = listOf(database.allergyDao().getAllRecords().first())
        }
    }

    fun addRecord(record: AllergyRecord) {
        viewModelScope.launch {
            database.allergyDao().insertRecord(record)
            loadRecords()
        }
    }

    fun deleteRecord(id: Int) {
        viewModelScope.launch {
            database.allergyDao().deleteRecord(id)
            loadRecords()
        }
    }
}