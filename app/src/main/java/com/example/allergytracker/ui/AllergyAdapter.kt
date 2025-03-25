import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.allergytracker.data.AllergyRecord

class AllergyAdapter : RecyclerView.Adapter<AllergyAdapter.AllergyViewHolder>() {
    private var records = emptyList<AllergyRecord>()

    fun submitList(newRecords: List<AllergyRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllergyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return AllergyViewHolder(view)
    }

    override fun onBindViewHolder(holder: AllergyViewHolder, position: Int) {
        val record = records[position]
        holder.bind(record)
    }

    override fun getItemCount() = records.size

    class AllergyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text1 = itemView.findViewById<TextView>(android.R.id.text1)
        private val text2 = itemView.findViewById<TextView>(android.R.id.text2)

        fun bind(record: AllergyRecord) {
            text1.text = record.date
            text2.text = "${record.symptoms} (${record.triggers})"
        }
    }
}