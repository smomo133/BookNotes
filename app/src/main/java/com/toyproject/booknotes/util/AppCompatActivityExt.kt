package com.toyproject.booknotes.util

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import dagger.android.support.DaggerAppCompatActivity

    fun AppCompatActivity.replaceFragmentInActivity(fragment: Fragment, @IdRes frameId: Int) {
        supportFragmentManager.transact {
            replace(frameId, fragment)
        }
    }

    /**
     * Runs a FragmentTransaction, then calls commit().
     */
    private inline fun FragmentManager.transact(action: FragmentTransaction.() -> Unit) {
        beginTransaction().apply {
            action()
        }.commit()
    }
