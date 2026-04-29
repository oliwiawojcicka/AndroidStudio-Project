package com.salle.grup17.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.salle.grup17.R
import com.salle.grup17.views.adapters.CharacterAdapter
import com.salle.grup17.api.RetrofitClient
import com.salle.grup17.models.ApiResponse
import com.salle.grup17.models.Character
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var charactersRecyclerView: RecyclerView
    private lateinit var characterAdapter: CharacterAdapter
    private val characterList = ArrayList<Character>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        charactersRecyclerView = view.findViewById(R.id.charactersRecyclerView)
        charactersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        characterAdapter = CharacterAdapter(characterList)
        charactersRecyclerView.adapter = characterAdapter

        loadCharacters()

        return view
    }

    private fun loadCharacters() {
        RetrofitClient.getApi().getCharacters(1)
            .enqueue(object : Callback<ApiResponse<Character>> {
                override fun onResponse(
                    call: Call<ApiResponse<Character>>,
                    response: Response<ApiResponse<Character>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        characterList.clear()
                        characterList.addAll(response.body()!!.results)
                        characterAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(requireContext(), "API error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Character>>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        "Connection error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}