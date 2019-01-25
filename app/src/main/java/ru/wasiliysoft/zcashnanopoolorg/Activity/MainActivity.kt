package ru.wasiliysoft.zcashnanopoolorg.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.*
import ru.wasiliysoft.zcashnanopoolorg.App
import ru.wasiliysoft.zcashnanopoolorg.Frafment.GeneralFragment
import ru.wasiliysoft.zcashnanopoolorg.Prefs
import ru.wasiliysoft.zcashnanopoolorg.R

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var fm: FragmentManager? = null
    internal val MENU_ADD_MINER = 1
    internal val MENU_SHARE = 3
    internal val MENU_CONTACT = 5
    private lateinit var pref: Prefs
    private lateinit var mDemoCollectionPagerAdapter: DemoCollectionPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
        settingNavigationView(navigationView.menu)

        pref = Prefs(this.applicationContext)
        // проверка списка майнеров
        if (App.getMiners().read().size == 0) {
            // майнеров нет
            showDialogAddMiner()
        } else {
            mDemoCollectionPagerAdapter = DemoCollectionPagerAdapter(supportFragmentManager)
            pager.adapter = mDemoCollectionPagerAdapter
            pager.setOnPageChangeListener(
                    object : ViewPager.SimpleOnPageChangeListener() {
                        override fun onPageSelected(position: Int) {
                            // When swiping between pages, select the
                            // corresponding tab.
                            updateTitle(position)
                        }
                    })
            pager.currentItem = pref.savedPageId
            updateTitle(pager.currentItem)
        }
    }

    override fun onPause() {
        super.onPause()
        pref.savedPageId = pager.currentItem
    }

    fun updateTitle(minerId: Int) {
        supportActionBar!!.title = App.getMiners().read()[minerId].name
        supportActionBar!!.subtitle = App.getMiners().read()[minerId].ticker
    }

    class DemoCollectionPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getCount(): Int = App.getMiners().read().size

        override fun getItem(i: Int): Fragment {
            return GeneralFragment.newInstance(i)
        }
    }

    /**
     * Показывает диалог добавления майнера
     */
    internal fun showDialogAddMiner() {
        startActivity(Intent(this, AddMinerActivity::class.java))
    }

    /**
     * Наполнение бокового меню
     */
    internal fun settingNavigationView(menu: Menu) {
        menu.add(0, MENU_ADD_MINER, 3, getString(R.string.add_miner)).setIcon(R.drawable.ic_add_black_24dp)
        menu.add(1, MENU_CONTACT, 5, R.string.feedback).setIcon(R.drawable.ic_email_black_24dp)
        menu.add(1, MENU_SHARE, 6, R.string.share_app).setIcon(R.drawable.ic_share_black_24dp)
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_delete_miner) {
            try {
                App.getMiners().delete(pager.currentItem)
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
            val i = Intent(this, MainActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(i)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == MENU_ADD_MINER) {
            showDialogAddMiner()
        } else if (id == MENU_SHARE) {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Try the app https://play.google.com/store/apps/details?id=$packageName")
            sendIntent.type = "text/plain"
            if (sendIntent.resolveActivity(packageManager) != null) {
                startActivity(sendIntent)
            }
        } else if (id == MENU_CONTACT) {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.type = "text/plain"
            intent.data = Uri.parse(getString(R.string.contact_email)) // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, "")
            intent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            }
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
