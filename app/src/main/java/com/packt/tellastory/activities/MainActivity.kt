package com.packt.tellastory.activities

import android.app.Fragment;
import android.content.Intent
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.packt.tellastory.R
import com.packt.tellastory.data.Repository
import com.packt.tellastory.fragments.StoriesFragment
import com.packt.tellastory.fragments.StoryContributeFragment
import com.packt.tellastory.fragments.StoryDetailFragment
import com.packt.tellastory.models.Story
import android.app.Activity
import com.packt.tellastory.AuthenticationHelper

class MainActivity : AppCompatActivity() {

    val REQUEST_LATE_ONBOARDING = 100

    val repository: Repository get() = Repository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        onList()
    }

    override fun onBackPressed() {
        val drawer = findViewById(R.id.drawer_layout) as DrawerLayout
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
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_LATE_ONBOARDING) {
            if (resultCode == Activity.RESULT_OK) {
                val story = data.getParcelableExtra<Story>(OnboardingActivity.ARG_STORY)
                val lastContribution = story.contributions.last()
                lastContribution.contributor = AuthenticationHelper.userName
                repository.updateContributions(story)
                onList()
            }
        }
    }

    fun onList() {
        val fragment = StoriesFragment.newInstance()
        showFragment(fragment)
    }

    fun onCreateStory() {
        val newStory = Story()
        newStory.lastUpdate = "today"
        val fragment = StoryContributeFragment.newInstance(newStory)
        showFragment(fragment)
    }

    fun onContribute(story: Story) {
        val fragment = StoryContributeFragment.newInstance(story)
        showFragment(fragment)
    }

    fun onReadStory(story: Story) {
        val fragment = StoryDetailFragment.newInstance(story)
        showFragment(fragment)
    }

    fun onLateOnboarding(story: Story) {
        val intent = Intent(this, OnboardingActivity::class.java)
        intent.putExtra(OnboardingActivity.ARG_LATE, true)
        intent.putExtra(OnboardingActivity.ARG_STORY, story)
        startActivityForResult(intent, REQUEST_LATE_ONBOARDING)
    }

    private fun showFragment(fragment: Fragment) {
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.main_fragment_container, fragment, fragment.javaClass.toString())
        ft.commit()
    }
}