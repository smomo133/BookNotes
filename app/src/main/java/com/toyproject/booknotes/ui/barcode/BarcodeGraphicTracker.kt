/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.toyproject.booknotes.ui.barcode

import android.content.Context
import androidx.annotation.UiThread
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.barcode.Barcode
import com.toyproject.booknotes.ui.camera.GraphicOverlay

class BarcodeGraphicTracker internal constructor(private val mOverlay: GraphicOverlay<BarcodeGraphic>,
                                                 private val mGraphic: BarcodeGraphic,
                                                 context: Context) : Tracker<Barcode>() {

    private var mBarcodeUpdateListener: BarcodeUpdateListener? = null

    /**
     * Consume the item instance detected from an Activity or Fragment level by implementing the
     * BarcodeUpdateListener interface method onBarcodeDetected.
     */
    interface BarcodeUpdateListener {
        @UiThread
        fun onBarcodeDetected(barcode: Barcode?)
    }

    init {
        if (context is BarcodeUpdateListener) {
            this.mBarcodeUpdateListener = context
        } else {
            throw RuntimeException("Hosting activity must implement BarcodeUpdateListener")
        }
    }

    /**
     * Start tracking the detected item instance within the item overlay.
     */
    override fun onNewItem(id: Int, item: Barcode?) {
        mGraphic.id = id
        mBarcodeUpdateListener!!.onBarcodeDetected(item)
    }

    /**
     * Update the position/characteristics of the item within the overlay.
     */
    override fun onUpdate(detectionResults: Detector.Detections<Barcode>?, item: Barcode) {
        mOverlay.add(mGraphic)
        mGraphic.updateItem(item)
    }

    /**
     * Hide the graphic when the corresponding object was not detected.  This can happen for
     * intermediate frames temporarily, for example if the object was momentarily blocked from
     * view.
     */
    override fun onMissing(detectionResults: Detector.Detections<Barcode>?) {
        mOverlay.remove(mGraphic)
    }

    /**
     * Called when the item is assumed to be gone for good. Remove the graphic annotation from
     * the overlay.
     */
    override fun onDone() {
        mOverlay.remove(mGraphic)
    }
}