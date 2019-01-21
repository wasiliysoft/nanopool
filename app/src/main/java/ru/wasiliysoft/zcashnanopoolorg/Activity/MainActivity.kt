package ru.wasiliysoft.zcashnanopoolorg.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.app_bar_main.*
import ru.wasiliysoft.zcashnanopoolorg.App
import ru.wasiliysoft.zcashnanopoolorg.Frafment.GeneralFragment
import ru.wasiliysoft.zcashnanopoolorg.R

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var fm: FragmentManager? = null
    internal val MENU_ADD_MINER = 1
    internal val MENU_SELECT_MINER = 2
    internal val MENU_SHARE = 3
    internal val MENU_DONATE = 4
    internal val MENU_CONTACT = 5

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

        fm = supportFragmentManager
        //        if (BuildConfig.DEBUG) {
        //            Miner m = new Miner("Test XMR", "xmr", "0x66fba1130f624680e4164fd3aa998b569ff01b60");
        //            App.getMiners().add(m);
        //        }
        // проверка списка майнеров
        if (App.getMiners().read().size == 0) {
            // майнеров нет
            showDialogAddMiner()
        } else {
            // показываем данные первого майнера в списке
//            val m = App.getMiners().read().firstEntry().value
//            val arg = Bundle()
//            arg.putSerializable(BUNDLE_MINER_ID, m)
//            ActivateGeneralFragment(arg)

            mDemoCollectionPagerAdapter = DemoCollectionPagerAdapter(supportFragmentManager)

            pager.setOffscreenPageLimit(1)
            pager.adapter = mDemoCollectionPagerAdapter
            pager.setOnPageChangeListener(
                    object : ViewPager.SimpleOnPageChangeListener() {
                        override fun onPageSelected(position: Int) {
                            // When swiping between pages, select the
                            // corresponding tab.
                            updateTitle(position)
                        }
                    })
            updateTitle(pager.currentItem)
        }
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
        startActivityForResult(Intent(this, AddMinerActivity::class.java), REQUEST_CODE_ADD_MINER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_MINER && resultCode == Activity.RESULT_OK) {
            finish()
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    /**
     * Наполнение бокового меню
     */
    internal fun settingNavigationView(menu: Menu) {
//        for ((key, value) in App.getMiners().read()) {
//            menu.add(0, MENU_SELECT_MINER, 1, value.name).setIcon(R.drawable.ic_poll_black_24dp)
//                    .setOnMenuItemClickListener {
//                        val arg = Bundle()
//                        arg.putSerializable(BUNDLE_MINER_ID, value)
//                        ActivateGeneralFragment(arg)
//                        false
//                    }
//        }

        menu.add(0, MENU_ADD_MINER, 3, getString(R.string.add_miner)).setIcon(R.drawable.ic_add_black_24dp)
        menu.add(1, MENU_CONTACT, 5, R.string.feedback).setIcon(R.drawable.ic_email_black_24dp)
        menu.add(1, MENU_SHARE, 6, R.string.share_app).setIcon(R.drawable.ic_share_black_24dp)
    }

//    /**
//     * Добавлет фрагемент в контейнер
//     */
//    internal fun ActivateGeneralFragment(arg: Bundle) {
//        val f = GeneralFragment()
//        f.arguments = arg
//        fm!!.beginTransaction()
//                .replace(R.id.fragmentContainer, f)
//                .commit()
//    }

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
            App.getMiners().delete(pager.currentItem)
            finish()
            startActivity(Intent(this, MainActivity::class.java))
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

    companion object {

        private val REQUEST_CODE_ADD_MINER = 1

    }

}
