package com.salle.grup17.controllers

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.salle.grup17.R
import com.salle.grup17.api.RetrofitClient
import com.salle.grup17.models.Character
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.Button

class CharacterDetailActivity : AppCompatActivity() {

    private lateinit var detailImage: ImageView
    private lateinit var detailName: TextView
    private lateinit var detailInfo: TextView
    private lateinit var backBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_detail)

        detailImage = findViewById(R.id.detailImage)
        detailName = findViewById(R.id.detailName)
        detailInfo = findViewById(R.id.detailInfo)
        backBtn = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            finish()
        }

        val characterId = intent.getIntExtra("character_id", -1)

        if (characterId == -1) {
            Toast.makeText(this, "Character ID error", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadCharacter(characterId)
    }

    private fun loadCharacter(id: Int) {
        RetrofitClient.getApi().getCharacterById(id)
            .enqueue(object : Callback<Character> {
                override fun onResponse(call: Call<Character>, response: Response<Character>) {
                    if (response.isSuccessful && response.body() != null) {
                        val character = response.body()!!

                        detailName.text = character.name

                        detailInfo.text = """
                            Status: ${character.status}
                            Species: ${character.species}
                            Type: ${if (character.type.isNullOrEmpty()) "Unknown" else character.type}
                            Gender: ${character.gender}
                            Origin: ${character.origin?.name}
                            Location: ${character.location?.name}
                        """.trimIndent()

                        Glide.with(this@CharacterDetailActivity)
                            .load(character.image)
                            .into(detailImage)
                    } else {
                        Toast.makeText(this@CharacterDetailActivity, "API error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Character>, t: Throwable) {
                    Toast.makeText(this@CharacterDetailActivity, "Connection error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }
}