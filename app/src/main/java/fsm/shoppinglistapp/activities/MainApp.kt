package fsm.shoppinglistapp.activities

import android.app.Application
import fsm.shoppinglistapp.db.MainDataBase

class MainApp : Application() {
    val database by lazy { MainDataBase.getDataBase(this) }
}