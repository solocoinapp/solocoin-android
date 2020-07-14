package app.solocoin.solocoin.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import app.solocoin.solocoin.R

class AllScratchCardsActivity : AppCompatActivity() {
    private lateinit var context: AllScratchCardsActivity
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_scratch_cards)
        context = this
        recyclerView = findViewById(R.id.allrewards_recycler_view)
    }
}