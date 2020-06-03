package com.prasanna.mynote

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.ticcket.view.*

class MainActivity : AppCompatActivity() {

    var listNotes=ArrayList<Notes>()
    var dbManager:NotesDatabase?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        dbManager = DbManager().getDatabase(application)
        Toast.makeText(this,"onCreate",Toast.LENGTH_LONG).show()

        //Load from DB
        LoadQuery("%")
    }

    override  fun onResume() {
        super.onResume()
        LoadQuery("%")
        Toast.makeText(this,"onResume",Toast.LENGTH_LONG).show()
    }

    fun LoadQuery(title:String){
        listNotes = dbManager!!.NotesDao().loadByTitle(title) as ArrayList<Notes>
        val myNotesAdapter= MyNotesAdpater(this, listNotes)
        lvNotes.adapter=myNotesAdapter
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu, menu)

        val sv: SearchView = menu.findItem(R.id.Searchid).actionView as SearchView

        val sm= getSystemService(Context.SEARCH_SERVICE) as SearchManager
        sv.setSearchableInfo(sm.getSearchableInfo(componentName))
        sv.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                Toast.makeText(applicationContext, query, Toast.LENGTH_LONG).show()
                LoadQuery("%$query%")
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })


        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item != null) {
            when(item.itemId){
                R.id.lvNotes->{
                    //Got to add paage
                    var intent= Intent(this,AddNotes::class.java)
                    startActivity(intent)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }


    inner class  MyNotesAdpater:BaseAdapter{
        var listNotesAdpater=ArrayList<Notes>()
        var context:Context?=null
        constructor(context:Context, listNotesAdpater:ArrayList<Notes>):super(){
            this.listNotesAdpater=listNotesAdpater
            this.context=context
        }

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

            var myView=layoutInflater.inflate(R.layout.ticcket,null)

            var myNote=listNotesAdpater[p0]
            myView.tvTitle.text=myNote.Title
            myView.tvDesc.text=myNote.Description
            myView.ivDelete.setOnClickListener{

                dbManager!!.NotesDao().delete(myNote)

                LoadQuery("%")
            }
            myView.ivEdit.setOnClickListener{

                GoToUpdate(myNote)

            }
            return myView
        }

        override fun getItem(p0: Int): Any {
            return listNotesAdpater[p0]
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdpater.size
        }
    }


    fun GoToUpdate(note:Notes){
        var intent=  Intent(this,AddNotes::class.java)
        intent.putExtra("ID",note.ID)
        intent.putExtra("name",note.Title)
        intent.putExtra("des",note.Description)
        startActivity(intent)
    }
}