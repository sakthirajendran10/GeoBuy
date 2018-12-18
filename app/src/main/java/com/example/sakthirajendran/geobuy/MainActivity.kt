package com.example.sakthirajendran.geobuy

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.support.v4.content.ContextCompat
import android.view.Menu
import android.widget.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mikepenz.actionitembadge.library.ActionItemBadge


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    var module: String? = null

    private var homeLayout: LinearLayout? = null
    private var accountll: LinearLayout? = null
    private var nearbyll: LinearLayout? = null
    private var categoryll: LinearLayout? = null

    private var homeView: ImageView? = null
    private var homeText: TextView? = null

    private var categoryView: ImageView? = null
    private var categoryText: TextView? = null

    private var nearByView: ImageView? = null
    private var nearByText: TextView? = null


    private val primeView: ImageView? = null
    private val primeText: TextView? = null

    private var accountView: ImageView? = null
    private var accountText: TextView? = null

    private var selectedPos: Int = 0

    var mapFragment: MapFragment? = null

    var categoryFragment: CategoryFragment? = null

    var homeFragment: HomeFragment? = null

//    var userFragment: UserFragment? = null

    var sessionManager: SessionManager? = null
    var menu: Menu? = null
    lateinit var userDetails: Map<String, *>
    //
    lateinit var navigationView: NavigationView

    var isBannerRunning = false
        private set


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.title = "Geobuy"
        setSupportActionBar(toolbar)


        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        var navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

        var menu = navigationView.menu

        manageBottomToolBar()
        setViewSelected(1)

        manageSearchBar()
        var sessionManager = SessionManager(this)
        userDetails = sessionManager!!.userDetails
    }

    private fun manageSearchBar() {
        val searchButton = findViewById<Button>(R.id.geobuy_search)
        searchButton.setOnClickListener { moveToSearch() }
    }

    private fun moveToSearch() {
//        val intent = Intent(this, SearchActivity::class.java)
//        startActivity(intent)
    }

    private fun manageBottomToolBar() {
        homeLayout = findViewById(R.id.home_layout) as LinearLayout
        homeView = findViewById(R.id.home_button) as ImageView
        homeText = findViewById(R.id.home_button_text) as TextView
        homeLayout!!.setOnClickListener { setViewSelected(3) }
        homeView!!.setOnClickListener { setViewSelected(3) }

        categoryll = findViewById(R.id.category_ll)  as LinearLayout
        categoryView = findViewById(R.id.category_button) as ImageView
        categoryText = findViewById(R.id.category_text) as TextView
        categoryll!!.setOnClickListener { setViewSelected(2) }
        categoryView!!.setOnClickListener { setViewSelected(2) }

        nearbyll = findViewById(R.id.nearby_ll) as LinearLayout
        nearByView = findViewById(R.id.nearby_button) as ImageView
        nearByText = findViewById(R.id.nearby_button_text) as TextView
        nearbyll!!.setOnClickListener { setViewSelected(1) }
        nearByView!!.setOnClickListener { setViewSelected(1) }
        /*primeView = findViewById(R.id.prime_button);
        primeText = findViewById(R.id.prime_button_text);
        primeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setViewSelected(4);
            }
        });
*/
        accountll = findViewById(R.id.account_ll) as LinearLayout
        accountView = findViewById(R.id.account_button) as ImageView
        accountText = findViewById(R.id.account_button_text) as TextView
        accountll!!.setOnClickListener { setViewSelected(5) }
        accountView!!.setOnClickListener { setViewSelected(5) }
    }

    private fun setViewSelected(position: Int) {
        //
        val appBarLayout = findViewById<LinearLayout>(R.id.default_app_bar)
        val welcomeBarLayout = findViewById<LinearLayout>(R.id.welcometoolbarLaout)
        if (selectedPos != position) {
            //  Toast.makeText(this, "TESTTTt", Toast.LENGTH_LONG).show();
            deSelectOthers(position)
            when (position) {
                1 -> {
                    hideView(welcomeBarLayout)
                    showView(appBarLayout)
                    val fm = supportFragmentManager
                    if (mapFragment == null)
                        mapFragment = MapFragment()
                    fm.beginTransaction().replace(R.id.dashboard_content, mapFragment!!).commit()
                    val mIcon = ContextCompat.getDrawable(this, R.drawable.nearby_primary)
                    nearByView!!.setImageDrawable(mIcon)
                    nearByText!!.setTextColor(resources.getColor(R.color.colorPrimary))
                }
                2 -> {
                    hideView(welcomeBarLayout)
                    showView(appBarLayout)
                    val fm = supportFragmentManager
                    if (categoryFragment == null)
                        categoryFragment = CategoryFragment()
                    fm.beginTransaction().replace(R.id.dashboard_content, categoryFragment!!).commit()
                    val mIcon = ContextCompat.getDrawable(this, R.drawable.category_primary)
                    categoryView!!.setImageDrawable(mIcon)
                    categoryText!!.setTextColor(resources.getColor(R.color.colorPrimary))
                }
                3 -> {
                    hideView(welcomeBarLayout)
                    showView(appBarLayout)
                    val fm = supportFragmentManager
                    if (homeFragment == null)
                        homeFragment = HomeFragment()
                    fm.beginTransaction().replace(R.id.dashboard_content, homeFragment!!).commit()
                    val mIcon = ContextCompat.getDrawable(this, R.drawable.home_primary)
                    homeView!!.setImageDrawable(mIcon)
                    homeText!!.setTextColor(resources.getColor(R.color.colorPrimary))
                }
                4 -> {
                    hideView(welcomeBarLayout)
                    showView(appBarLayout)
                    val mIcon = ContextCompat.getDrawable(this, R.drawable.prime_primary)
                }/* primeView.setImageDrawable(mIcon);
                    primeText.setTextColor(getResources().getColor(R.color.colorPrimary));*/
                5 -> {
                    hideView(appBarLayout)
                    showView(welcomeBarLayout)
                    val fm = supportFragmentManager
//                    if (userFragment == null)
//                        userFragment = UserFragment()
//                    fm.beginTransaction().replace(R.id.dashboard_content, userFragment).commit()
//                    val mIcon = ContextCompat.getDrawable(this, R.drawable.account_primary)
//                    accountView!!.setImageDrawable(mIcon)
//                    accountText!!.setTextColor(resources.getColor(R.color.colorPrimary))
                }
            }
            selectedPos = position
        }

    }

    private fun deSelectOthers(selectedPosition: Int) {
        if (selectedPosition != 1) {
            val mIcon = ContextCompat.getDrawable(this, R.drawable.nearbyblack)
            nearByView!!.setImageDrawable(mIcon)
            nearByText!!.setTextColor(resources.getColor(R.color.black_overlay))
        }

        if (selectedPosition != 2) {
            val cIcon = ContextCompat.getDrawable(this, R.drawable.category_black)
            categoryView!!.setImageDrawable(cIcon)
            categoryText!!.setTextColor(resources.getColor(R.color.black_overlay))
        }

        if (selectedPosition != 3) {
            val hIcon = ContextCompat.getDrawable(this, R.drawable.home_black)
            homeView!!.setImageDrawable(hIcon)
            homeText!!.setTextColor(resources.getColor(R.color.black_overlay))
        }

        if (selectedPosition != 4) {
            val pIcon = ContextCompat.getDrawable(this, R.drawable.prime_black)
            /*primeView.setImageDrawable(pIcon);
            primeText.setTextColor(getResources().getColor(R.color.black_overlay));*/
        }

        if (selectedPosition != 5) {
            val aIcon = ContextCompat.getDrawable(this, R.drawable.account_black)
            accountView!!.setImageDrawable(aIcon)
            accountText!!.setTextColor(resources.getColor(R.color.black_overlay))
        }

    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)

        // Read your drawable from somewhere
        val dr = resources.getDrawable(R.drawable.kart)
        val bitmap = (dr as BitmapDrawable).bitmap
        // Scale it to 50 x 50
        val d = BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, 75, 75, true))
        // Set your new, scaled drawable "d"
        val badgeStyle = ActionItemBadge.BadgeStyles.DARK_GREY.style
        val menuItem = menu.findItem(R.id.item_samplebadge)
        val orgService = OrgService()
        val count = orgService.getCartItems(this)
        ActionItemBadge.update(this, menuItem, d, badgeStyle, count)
        //getCategoryMaster();
        return true
    }

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId

        if (id == R.id.item_samplebadge) {
//            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        } else if (id == R.id.my_cart) {
            if (userDetails["useremail"] != null) {
//                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            } else {
//                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
            }
        } else if (id == R.id.my_orders) {
            if (userDetails["useremail"] != null) {
//                val intent = Intent(this, OrderDetailsActivity::class.java)
                startActivity(intent)
            } else {
//                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
            }
        } else if (id == R.id.wish_list) {
            if (userDetails["useremail"] != null) {
//                val intent = Intent(this, WishListActivity::class.java)
                startActivity(intent)
            } else {
//                val intent = Intent(this, SigninActivity::class.java)
                startActivity(intent)
            }

        } else if (id == R.id.notifications || id == R.id.action_notifications) {
//            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        when (item.itemId) {
            R.id.item_samplebadge -> {
//                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_notifications -> {
//                val intent = Intent(this, NotificationActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showView(vararg views: View) {
        for (v in views) {
            v.visibility = View.VISIBLE

        }

    }

    private fun hideView(vararg views: View) {
        for (v in views) {
            v.visibility = View.GONE

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GeobuyConstants.REQUEST_LOCATION) {
            mapFragment!!.onActivityResult(requestCode, resultCode, data)
        } else if (requestCode == GeobuyConstants.HOME_REQUEST_LOCATION) {
            homeFragment!!.onActivityResult(requestCode, resultCode, data)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
    public override fun onResume() {
        super.onResume()
        sessionManager = null
        sessionManager = SessionManager(this)
        userDetails = sessionManager!!.userDetails
        // put your code here...
        if (menu != null) {
            val count = OrgService().getCartItems(this)
            //val actionItemBadge = ActionItemBadge()
            ActionItemBadge.update(menu!!.findItem(R.id.item_samplebadge), count)
        }
    }


    private fun formUiforCategory(categoryJson: String) {
        toast(categoryJson)
        val gson = Gson()
        val type = object : TypeToken<List<CategoryMaster>>() {

        }.type
        val cts = gson.fromJson<List<CategoryMaster>>(categoryJson, type)
        val subMenu = menu!!.addSubMenu("Categories")
        var counter = 0
        for (categoryMaster in cts) {
            subMenu.add(counter, Menu.FIRST + counter, Menu.FIRST, categoryMaster.name)
            counter++
        }
        navigationView.invalidate()
        //  invalidateOptionsMenu();

    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    fun setRunning(isRunning: Boolean) {
        this.isBannerRunning = isRunning
    }
}